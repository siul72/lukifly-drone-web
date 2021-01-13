package co.luism.iot.web.ui.vehicle.utils;

import co.luism.diagnostics.common.EventTypeEnum;
import co.luism.diagnostics.enterprise.*;
import co.luism.iot.web.ui.vehicle.charts.OnDiagChart;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import org.apache.log4j.Logger;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;

import java.util.*;

/**
 * Created by luis on 03.12.14.
 */
public final class AlarmUtils {

    private static final Logger LOG = Logger.getLogger(AlarmUtils.class);
    AlarmUtils(){

    }

    public static List<OnDiagChart> createChartList(Integer alarmValueHistoryInfoId){

        List<OnDiagChart> myChartList = new ArrayList<>();

        Map<String, Object> myRestrictions = new HashMap<>();
        myRestrictions.put("alarmTagHistoryInfoId", alarmValueHistoryInfoId);

        List<AlarmEnvironmentData> alarmEnvironmentDataList = AlarmEnvironmentData.getList(AlarmEnvironmentData.class,
                myRestrictions);

        if(alarmEnvironmentDataList == null){
            return null;
        }

        //get count distinct categories
        Map<Integer, List<AlarmEnvironmentData>> categoryEnvDataMap = new HashMap<>();

        for(AlarmEnvironmentData environmentData : alarmEnvironmentDataList){
            Integer catIndex = environmentData.getCategoryIndex();
            List<AlarmEnvironmentData> currentList = categoryEnvDataMap.get(catIndex);
            if(currentList == null){
                currentList = new ArrayList<>();
                categoryEnvDataMap.put(catIndex, currentList);
            }

            currentList.add(environmentData);
        }

        for(Integer catIndex : categoryEnvDataMap.keySet()){
            myRestrictions.clear();
            myRestrictions.put("categoryIndex", catIndex);
            //get the category ID
            AlarmCategory c = AlarmCategory.read(AlarmCategory.class, myRestrictions);

            if(c == null){
                continue;
            }

            addChart(myChartList, c, categoryEnvDataMap.get(catIndex));
        }



        return myChartList;

    }

    private static void addChart(List<OnDiagChart> myChartList, AlarmCategory alarmCategory, List<AlarmEnvironmentData> alarmEnvironmentDatas) {

        Map<String, Object> myRestrictions = new HashMap<>();

        myRestrictions.clear();
        myRestrictions.put("categoryId", alarmCategory.getCategoryId());
        List<CategorySignalMap> mappingCategoryEventIds = CategorySignalMap.getList(CategorySignalMap.class, myRestrictions);

        if(mappingCategoryEventIds == null){
            return;
        }

        Map<Integer, CategorySignalMap> positionEventIdSizeMap = new TreeMap<>();
        Map<Integer, TimeSeries> positionTimeSeries = new TreeMap<>();


        for(CategorySignalMap s : mappingCategoryEventIds){

            positionEventIdSizeMap.put(s.getPosition(), s);

            DataTag t=  WebManagerFacade.getInstance().getTagBySourceId(EventTypeEnum.TAG_DATA_TYPE_EVENT, s.getCategoryId(),s.getSignalId());
            if(t == null){
                LOG.error("tag not found!!!!!!");
                continue;
            }

            //TimeSeries ts = new TimeSeries(t.getName());
            TimeSeries ts = new TimeSeries(t.getTagId());
            positionTimeSeries.put(s.getPosition(), ts);
        }

        for(AlarmEnvironmentData obj : alarmEnvironmentDatas){
            parseTransverseData( obj, positionEventIdSizeMap, positionTimeSeries);

        }

        OnDiagChart myChart = new OnDiagChart(alarmCategory.getName(), positionTimeSeries.values());

        if(myChart == null){
            return;
        }
        myChartList.add(myChart);
        LOG.debug("chart added for category "+ alarmCategory.getName());
    }

    private static void parseTransverseData(AlarmEnvironmentData alarmEnvironmentData,
                                            Map<Integer, CategorySignalMap> positionEventIdSizeMap,
                                            Map<Integer, TimeSeries> positionTimeSeries) {



        List<Integer> valueList = getValues(alarmEnvironmentData.getValue(), positionEventIdSizeMap);

        if(valueList == null){
            return;
        }

        long timeMs = (long)alarmEnvironmentData.getTimeStampMilliSeconds() + 1000 * (long)alarmEnvironmentData.getTimeStampSeconds();
        Date time = new Date(timeMs);

        for(Integer pos : positionTimeSeries.keySet()){
            TimeSeries ts = positionTimeSeries.get(pos);
            ts.add(new Millisecond(time), valueList.get(pos));
        }



    }

    private static List<Integer> getValues(String value, Map<Integer, CategorySignalMap> positionEventIdSizeMap) {

        List<Integer> myList = new ArrayList<>();
        //byte[] b = value.getBytes(Charset.forName("UTF-8"));

        int currentByte = 0, currentBit = 0;
        //we have to iterate by size in the position map
        for(CategorySignalMap s : positionEventIdSizeMap.values()){

            int nValue;
            try{
                nValue = Integer.parseInt(value.substring(currentByte, currentByte +2), 16);
            } catch (NumberFormatException e){
                LOG.error(e);
                continue;
            }


            switch (s.getSignalSize()){
                case 1:
                    myList.add(getBitValue(nValue, currentBit++));

                    if(currentBit > 7){
                        currentBit = 0;
                        currentByte++;
                    }
                break;

                case 8:
                    myList.add(nValue);

                    if(currentBit > 7){
                        currentBit = 0;
                        currentByte++;
                    }

                break;

                default:
                    return null;

            }

        }

        return myList;
    }

    private static Integer getBitValue(int value, int currentBit) {

        int mask = 1 << currentBit;
        mask = value & mask;

        return mask >> currentBit;
    }
}
