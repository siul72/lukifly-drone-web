/*! (C) 2014 Glyptodon LLC - glyptodon.org/MIT-LICENSE */
var Guacamole=Guacamole||{};

Guacamole.ArrayBufferReader=function(b){
    var a=this;b.onblob=function(f){
        var g=window.atob(f);
        var d=new ArrayBuffer(g.length);
        var e=new Uint8Array(d);
        for(var c=0;c<g.length;c++){
            e[c]=g.charCodeAt(c)
        }
        if(a.ondata){
            a.ondata(d)
        }
    };
    b.onend=function(){
        if(a.onend){
            a.onend()
        }
    };
    this.ondata=null;
    this.onend=null
};

var Guacamole=Guacamole||{};

Guacamole.ArrayBufferWriter=function(c){
    var b=this;c.onack=function(d){
        if(b.onack){b.onack(d)
        }
    };

    function a(d){
        var f="";
        for(var e=0;e<d.byteLength;e++){
            f+=String.fromCharCode(d[e])
        }
        c.sendBlob(window.btoa(f))
    }

    this.sendData=function(e){
        var d=new Uint8Array(e);
        if(d.length<=8064){
            a(d)
        }else{
            for(var f=0;f<d.length;f+=8064){
                a(d.subarray(f,f+8094))
            }
        }
    };

    this.sendEnd=function(){
        c.sendEnd()
    };
    this.onack=null
};

var Guacamole=Guacamole||{};

Guacamole.AudioChannel=function(){
    var b=this;
    var a=0;
    this.play=function(c,g,e){
        var f=new Guacamole.AudioChannel.Packet(c,e);
        var d=Guacamole.AudioChannel.getTimestamp();
        if(a<d){a=d}f.play(a);a+=g}
};

if(window.webkitAudioContext){
    Guacamole.AudioChannel.context=new webkitAudioContext()
}

Guacamole.AudioChannel.getTimestamp=function(){
    if(Guacamole.AudioChannel.context){
        return Guacamole.AudioChannel.context.currentTime*1000
    }

    if(window.performance){
        if(window.performance.now){
            return window.performance.now()
        }
        if(window.performance.webkitNow){
            return window.performance.webkitNow()
        }
    }
    return new Date().getTime()
};

Guacamole.AudioChannel.Packet=function(k,g){
    this.play=undefined;
    if(Guacamole.AudioChannel.context){
        var j=null;var e=function(l){j=l
        };
        var h=new FileReader();
        h.onload=function(){
            Guacamole.AudioChannel.context.decodeAudioData(h.result,function(l){e(l)
            })
        };
        h.readAsArrayBuffer(g);
        var a=Guacamole.AudioChannel.context.createBufferSource();
        a.connect(Guacamole.AudioChannel.context.destination);
        var d;
        function c(l){
            a.buffer=l;a.noteOn(d/1000)
        }
        this.play=function(l){
            d=l;
            if(j){
                c(j)}else{
                e=c
            }
        }
    }else{
        var i=false;
        var f=new Audio();
        var h=new FileReader();
        h.onload=function(){
            var n="";
            var l=new Uint8Array(h.result);
            for(var m=0;m<l.byteLength;m++){
                n+=String.fromCharCode(l[m])
            }
            f.src="data:"+k+";base64,"+window.btoa(n);
            if(i){f.play()}};h.readAsArrayBuffer(g);
        function b(){
            if(f.src){f.play()
            }else{
                i=true
            }
        }
        this.play=function(l){
            var n=Guacamole.AudioChannel.getTimestamp();
            var m=l-n;
            if(m<0){
                b()
            }else{
                window.setTimeout(b,m)
            }
        }
    }
};

var Guacamole=Guacamole||{};
Guacamole.BlobReader=function(e,a){
    var d=this;var c=0;var b;if(window.BlobBuilder){
        b=new BlobBuilder()}else{if(window.WebKitBlobBuilder){
        b=new WebKitBlobBuilder()}else{if(window.MozBlobBuilder){b=new MozBlobBuilder()}else{
        b=new (function(){
            var f=[];this.append=function(g){
                f.push(new Blob([g],{type:a}))};this.getBlob=function(){
                return new Blob(f,{
                    type:a})}})()}}}e.onblob=function(j){var k=window.atob(j);
        var g=new ArrayBuffer(k.length);
        var h=new Uint8Array(g);for(var f=0;f<k.length;f++){h[f]=k.charCodeAt(f)}b.append(g);
        c+=g.byteLength;
        if(d.onprogress){
            d.onprogress(g.byteLength)}e.sendAck("OK",0)
    };
    e.onend=function(){if(d.onend){d.onend()}};
    this.getLength=function(){
        return c
    };
    this.getBlob=function(){
        return b.getBlob()};
    this.onprogress=null;this.onend=null
};
var Guacamole=Guacamole||{};


Guacamole.Client=function(tunnel, port){
    var x=this;
    var d=0;
    var r=1;
    var f=2;
    var e=3;
    var a=4;
    var g=5;
    var A=d;
    var t=0;
    var k=null;
    var y={0:"butt",1:"round",2:"square"};
    var h={0:"bevel",1:"miter",2:"round"};
    var u=new Guacamole.Display();
    var j={};
    var i=[];
    var z=[];
    var q=[];
    var c=new Guacamole.IntegerPool();
    var n=[];
    var myPort = port;

    function s(B){
        if(B!=A){
            A=B;
            if(x.onstatechange){
                x.onstatechange(A)
            }
        }
    }

    function v(){
        return A==e||A==f
    }
    this.getDisplay=function(){
        return u
    };

    this.sendSize=function(C,B){
        if(!v()){return}
        tunnel.sendMessage("size",C,B)
    };

    this.sendKeyEvent=function(B,C){
        if(!v()){return
        }
        tunnel.sendMessage("key",C,B)
    };

    this.sendMouseState=function(C){
        if(!v()){
            return
        }
        u.moveCursor(Math.floor(C.x),Math.floor(C.y));
        var B=0;
        if(C.left){B|=1}
        if(C.middle){
            B|=2}if(C.right){
            B|=4}if(C.up){
            B|=8}if(C.down){
            B|=16}
        tunnel.sendMessage("mouse",Math.floor(C.x),Math.floor(C.y),B)
    };

    this.setClipboard=function(D){
        if(!v()){
            return
        }
        var E=x.createClipboardStream("text/plain");
        var C=new Guacamole.StringWriter(E);
        for(var B=0;B<D.length;B+=4096){
            C.sendText(D.substring(B,B+4096))
        }
        C.sendEnd()
    };

    this.createFileStream=function(B,C){var D=c.next();
        tunnel.sendMessage("file",D,B,C);
        var F=n[D]=new Guacamole.OutputStream(x,D);
        var E=F.sendEnd;
        F.sendEnd=function(){E();c.free(D);
            delete n[D]
        };
        return F
    };

    this.createPipeStream=function(B,D){
        var C=c.next();
        tunnel.sendMessage("pipe",C,B,D);
        var F=n[C]=new Guacamole.OutputStream(x,C);
        var E=F.sendEnd;F.sendEnd=function(){
            E();
            c.free(C);
            delete n[C]};
        return F
    };

    this.createClipboardStream=function(B){
        var C=c.next();
        tunnel.sendMessage("clipboard",C,B);
        var E=n[C]=new Guacamole.OutputStream(x,C);
        var D=E.sendEnd;
        E.sendEnd=function(){D();c.free(C);delete n[C]};return E};
    this.sendAck=function(B,D,C){if(!v()){return}
        tunnel.sendMessage("ack",B,D,C)};
    this.sendBlob=function(B,C){if(!v()){return}
        tunnel.sendMessage("blob",B,C)};
    this.endStream=function(B){if(!v()){return}
        tunnel.sendMessage("end",B)};
    this.onstatechange=null;
    this.onname=null;this.onerror=null;this.onclipboard=null;
    this.onfile=null;this.onpipe=null;this.onsync=null;function m(B){var C=j[B];
        if(!C){if(B===0){
            C=u.getDefaultLayer()
        }else{
            if(B>0){
                C=u.createLayer()
            }else{
                C=u.createBuffer()
            }
        }
            j[B]=C
        }
        return C
    }
    function b(B){
        var C=i[B];
        if(C==null){
            C=i[B]=new Guacamole.Parser();
            C.oninstruction=tunnel.oninstruction
        }
        return C
    }
    function o(B){
        var C=z[B];if(C==null){
            C=z[B]=new Guacamole.AudioChannel()
        }
        return C
    }
    var p={"miter-limit":function(B,C){u.setMiterLimit(B,parseFloat(C))}};
    var l={ack:function(C){
        var F=parseInt(C[0]);
        var D=C[1];var B=C[2];
        var E=n[F];
        if(E){if(E.onack){
            E.onack(new Guacamole.Status(B,D))
        }
            if(B>=256){
                c.free(F);
                delete n[F]
            }
        }
    },arc:function(H){
        var G=m(parseInt(H[0]));
        var C=parseInt(H[1]);
        var I=parseInt(H[2]);
        var B=parseInt(H[3]);
        var F=parseFloat(H[4]);
        var D=parseFloat(H[5]);
        var E=parseInt(H[6]);
        u.arc(G,C,I,B,F,D,E!=0)
    },audio:function(D){
        var G=parseInt(D[0]);
        var C=o(parseInt(D[1]));
        var B=D[2];
        var E=parseFloat(D[3]);
        var F=q[G]=new Guacamole.InputStream(x,G);
        var H=new Guacamole.BlobReader(F,B);
        H.onend=function(){
            C.play(B,E,H.getBlob())
        };
        x.sendAck(G,"OK",0)},blob:function(B){
        var E=parseInt(B[0]);
        var C=B[1];var D=q[E];D.onblob(C)},cfill:
        function(G){
            var H=parseInt(G[0]);
            var D=m(parseInt(G[1]));
            var F=parseInt(G[2]);var E=parseInt(G[3]);var B=parseInt(G[4]);
            var C=parseInt(G[5]);
            u.setChannelMask(D,H);u.fillColor(D,F,E,B,C)},clip:function(C){
        var B=m(parseInt(C[0]));u.clip(B)},clipboard:function(C){
        var E=parseInt(C[0]);var B=C[1];if(x.onclipboard){
            var D=q[E]=new Guacamole.InputStream(x,E);
            x.onclipboard(D,B)}else{
            x.sendAck(E,"Clipboard unsupported",256)
        }
    },close:function(C){
        var B=m(parseInt(C[0]));
        u.close(B)
    },copy:function(J){
        var B=m(parseInt(J[0]));
        var F=parseInt(J[1]);
        var E=parseInt(J[2]);
        var D=parseInt(J[3]);
        var K=parseInt(J[4]);
        var I=parseInt(J[5]);
        var C=m(parseInt(J[6]));
        var H=parseInt(J[7]);
        var G=parseInt(J[8]);
        u.setChannelMask(C,I);
        u.copy(B,F,E,D,K,C,H,G)
    },cstroke:function(J){
        var G=parseInt(J[0]);
        var E=m(parseInt(J[1])
        );
        var K=y[parseInt(J[2])];
        var C=h[parseInt(J[3])];
        var I=parseInt(J[4]);
        var B=parseInt(J[5]);
        var D=parseInt(J[6]);
        var F=parseInt(J[7]);
        var H=parseInt(J[8]);
        u.setChannelMask(E,G);
        u.strokeColor(E,K,C,I,B,D,F,H)},cursor:function(I){
        var H=parseInt(I[0]);var G=parseInt(I[1]);
        var E=m(parseInt(I[2]));
        var C=parseInt(I[3]);var B=parseInt(I[4]);
        var F=parseInt(I[5]);var D=parseInt(I[6]);
        u.setCursor(H,G,E,C,B,F,D)
    },curve:function(H){
        var G=m(parseInt(H[0]));var D=parseInt(H[1]);
        var C=parseInt(H[2]);var F=parseInt(H[3]);
        var E=parseInt(H[4]);var B=parseInt(H[5]);
        var I=parseInt(H[6]);u.curveTo(G,D,C,F,E,B,I)},dispose:function(D){
        var B=parseInt(D[0]);if(B>0){var C=m(B);C.dispose();
            delete j[B]}else{if(B<0){delete j[B]}}},distort:
        function(J){
            var B=parseInt(J[0]);var I=parseFloat(J[1]);var H=parseFloat(J[2]);var G=parseFloat(J[3]);var F=parseFloat(J[4]);var E=parseFloat(J[5]);var D=parseFloat(J[6]);if(B>=0){var C=m(B);C.distort(I,H,G,F,E,D)}},error:function(C){var D=C[0];var B=C[1];if(x.onerror){x.onerror(new Guacamole.Status(B,D))}
        x.disconnect()
    },end:function(B){var D=parseInt(B[0]);var C=q[D];if(C.onend){C.onend()}},file:function(D){var F=parseInt(D[0]);var B=D[1];var C=D[2];if(x.onfile){var E=q[F]=new Guacamole.InputStream(x,F);x.onfile(E,B,C)}else{x.sendAck(F,"File transfer unsupported",256)}},identity:function(C){var B=m(parseInt(C[0]));u.setTransform(B,1,0,0,1,0,0)},lfill:function(D){var E=parseInt(D[0]);var C=m(parseInt(D[1]));var B=m(parseInt(D[2]));u.setChannelMask(C,E);u.fillLayer(C,B)},line:function(D){var C=m(parseInt(D[0]));var B=parseInt(D[1]);var E=parseInt(D[2]);u.lineTo(C,B,E)},lstroke:function(D){var E=parseInt(D[0]);var C=m(parseInt(D[1]));var B=m(parseInt(D[2]));u.setChannelMask(C,E);u.strokeLayer(C,B)},move:function(G){var C=parseInt(G[0]);var D=parseInt(G[1]);var B=parseInt(G[2]);var I=parseInt(G[3]);var H=parseInt(G[4]);if(C>0&&D>=0){var E=m(C);var F=m(D);E.move(F,B,I,H)}},name:function(B){if(x.onname){x.onname(B[0])}},nest:function(B){var C=b(parseInt(B[0]));C.receive(B[1])},pipe:function(D){var F=parseInt(D[0]);var B=D[1];var C=D[2];if(x.onpipe){var E=q[F]=new Guacamole.InputStream(x,F);x.onpipe(E,B,C)}else{x.sendAck(F,"Named pipes unsupported",256)}},png:function(D){var G=parseInt(D[0]);var C=m(parseInt(D[1]));var B=parseInt(D[2]);var F=parseInt(D[3]);var E=D[4];u.setChannelMask(C,G);u.draw(C,B,F,"data:image/png;base64,"+E)},pop:function(C){var B=m(parseInt(C[0]));u.pop(B)},push:function(C){var B=m(parseInt(C[0]));u.push(B)},rect:function(F){var D=m(parseInt(F[0]));var B=parseInt(F[1]);var G=parseInt(F[2]);var C=parseInt(F[3]);var E=parseInt(F[4]);u.rect(D,B,G,C,E)},reset:function(C){var B=m(parseInt(C[0]));u.reset(B)},set:function(E){var C=m(parseInt(E[0]));var B=E[1];var F=E[2];var D=p[B];if(D){D(C,F)}},shade:function(E){var B=parseInt(E[0]);var C=parseInt(E[1]);if(B>=0){var D=m(B);D.shade(C)}},size:function(F){var C=parseInt(F[0]);var D=m(C);var E=parseInt(F[1]);var B=parseInt(F[2]);u.resize(D,E,B)},start:function(D){var C=m(parseInt(D[0]));var B=parseInt(D[1]);var E=parseInt(D[2]);u.moveTo(C,B,E)},sync:function(C){var D=parseInt(C[0]);u.flush(function B(){if(D!==t){tunnel.sendMessage("sync",D);t=D}});if(A===f){s(e)}if(x.onsync){x.onsync(D)}},transfer:function(J){var B=m(parseInt(J[0]));var G=parseInt(J[1]);var F=parseInt(J[2]);var E=parseInt(J[3]);var K=parseInt(J[4]);var D=parseInt(J[5]);var C=m(parseInt(J[6]));
        var I=parseInt(J[7]);var H=parseInt(J[8]);if(D===3){u.put(B,G,F,E,K,C,I,H)}else{if(D!==5){u.transfer(B,G,F,E,K,C,I,H,Guacamole.Client.DefaultTransferFunction[D])}}},transform:function(E){var D=m(parseInt(E[0]));var C=parseFloat(E[1]);var B=parseFloat(E[2]);var I=parseFloat(E[3]);var H=parseFloat(E[4]);var G=parseFloat(E[5]);var F=parseFloat(E[6]);u.transform(D,C,B,I,H,G,F)},video:function(D){var G=parseInt(D[0]);var C=m(parseInt(D[1]));var B=D[2];var E=parseFloat(D[3]);var F=q[G]=new Guacamole.InputStream(x,G);var H=new Guacamole.BlobReader(F,B);H.onend=function(){var I=new FileReader();I.onload=function(){var L="";var J=new Uint8Array(I.result);for(var K=0;K<J.byteLength;K++){L+=String.fromCharCode(J[K])}C.play(B,E,"data:"+B+";base64,"+window.btoa(L))};I.readAsArrayBuffer(H.getBlob())};tunnel.sendMessage("ack",G,"OK",0)}};tunnel.oninstruction=function(D,C){var B=l[D];if(B){B(C)}};

    this.disconnect=function(){
        if(A!=g&&A!=a){
            s(a);
            if(k){window.clearInterval(k)}
            tunnel.sendMessage("disconnect");
            tunnel.disconnect();
            s(g)
        }
    };

    this.connect=function(C){
        s(r);
        try{
            tunnel.connect(C, myPort)
        }
        catch(B)
        {
            s(d);
            throw B
        }

        k=window.setInterval(function(){
            tunnel.sendMessage("sync",t)
        },5000);
        s(f)
    }
};

Guacamole.Client.DefaultTransferFunction={0:function(a,b){b.red=b.green=b.blue=0},15:function(a,b){b.red=b.green=b.blue=255},3:function(a,b){b.red=a.red;b.green=a.green;b.blue=a.blue;b.alpha=a.alpha},5:function(a,b){},12:function(a,b){b.red=255&~a.red;b.green=255&~a.green;b.blue=255&~a.blue;b.alpha=a.alpha},10:function(a,b){b.red=255&~b.red;b.green=255&~b.green;b.blue=255&~b.blue},1:function(a,b){b.red=(a.red&b.red);b.green=(a.green&b.green);b.blue=(a.blue&b.blue)},14:function(a,b){b.red=255&~(a.red&b.red);b.green=255&~(a.green&b.green);b.blue=255&~(a.blue&b.blue)},7:function(a,b){b.red=(a.red|b.red);b.green=(a.green|b.green);b.blue=(a.blue|b.blue)},8:function(a,b){b.red=255&~(a.red|b.red);b.green=255&~(a.green|b.green);b.blue=255&~(a.blue|b.blue)},6:function(a,b){b.red=(a.red^b.red);b.green=(a.green^b.green);b.blue=(a.blue^b.blue)},9:function(a,b){b.red=255&~(a.red^b.red);b.green=255&~(a.green^b.green);b.blue=255&~(a.blue^b.blue)},4:function(a,b){b.red=255&(~a.red&b.red);b.green=255&(~a.green&b.green);b.blue=255&(~a.blue&b.blue)},13:function(a,b){b.red=255&(~a.red|b.red);b.green=255&(~a.green|b.green);b.blue=255&(~a.blue|b.blue)},2:function(a,b){b.red=255&(a.red&~b.red);b.green=255&(a.green&~b.green);b.blue=255&(a.blue&~b.blue)},11:function(a,b){b.red=255&(a.red|~b.red);b.green=255&(a.green|~b.green);b.blue=255&(a.blue|~b.blue)}};var Guacamole=Guacamole||{};

Guacamole.Display=function(){
    var m=this;
    var p=0;
    var d=0;
    var c=1;
    var j=document.createElement("div");
    j.style.position="relative";
    j.style.width=p+"px";
    j.style.height=d+"px";
    j.style.transformOrigin=
        j.style.webkitTransformOrigin=j.style.MozTransformOrigin=
            j.style.OTransformOrigin=j.style.msTransformOrigin="0 0";
    var h=new Guacamole.Display.VisibleLayer(p,d);
    var n=new Guacamole.Display.VisibleLayer(0,0);
    n.setChannelMask(Guacamole.Layer.SRC);
    j.appendChild(h.getElement());j.appendChild(n.getElement());
    var a=document.createElement("div");
    a.style.position="relative";
    a.style.width=(p*c)+"px";
    a.style.height=(d*c)+"px";
    a.appendChild(j);
    this.cursorHotspotX=0;
    this.cursorHotspotY=0;
    this.cursorX=0;this.cursorY=0;
    this.onresize=null;
    var e=[];
    var i=[];

    function l(){
        var q=0;
        //console.log("attempting flush of %i frames",i.length);
        while(q<i.length){
            var r=i[q];if(!r.isReady()){
                //console.log("frame %i is not ready",q);
                break
            }
            r.flush();
            q++
        }
        i.splice(0,q);
        //console.log("%i frames flushed, %i frames remain",q,i.length)
    }

    function o(r,q){
        this.isReady=function(){
            for(var s=0;s<q.length;s++){
                if(q[s].blocked){
                    return false
                }
            }
            return true
        };
        this.flush=function(){
            for(var s=0;s<q.length;s++){q[s].execute()}if(r){r()
            }
        }
    }
    var b=0;
    function g(s,r){
        var q=this;
        this.blocked=r;
        if(r){
            b++;
            //console.log("%i blocked tasks (+1)",b)
        }

        this.unblock=function(){
            if(q.blocked){
                b--;
                //console.log("%i blocked tasks (-1)",b);
                q.blocked=false;l()
            }
        };
        this.execute=function(){if(s){s()
        }
        }
    }
    function k(s,r){
        var q=new g(s,r);e.push(q);
        return q
    }
    this.getElement=function(){return a};
    this.getWidth=function(){return p};
    this.getHeight=function(){return d};
    this.getDefaultLayer=function(){return h};
    this.getCursorLayer=function(){return n};

    this.createLayer=function(){
        var q=new Guacamole.Display.VisibleLayer(p,d);
        q.move(h,0,0,0);
        return q
    };

    this.createBuffer=function(){
        var q=new Guacamole.Layer(0,0);
        q.autosize=1;
        return q
    };

    this.flush=function(q){
        i.push(new o(q,e));
        e=[];
        //console.log("new frame containing %i tasks",e.length);
        l()
    };

    this.setCursor=function(w,u,v,r,q,t,x){
        k(function s(){
            m.cursorHotspotX=w;
            m.cursorHotspotY=u;
            n.resize(t,x);
            n.copy(v,r,q,t,x,0,0);
            m.moveCursor(m.cursorX,m.cursorY)})
    };
    this.moveCursor=function(q,r){
        n.translate(q-m.cursorHotspotX,r-m.cursorHotspotY);
        m.cursorX=q;m.cursorY=r
    };

    this.resize=function(r,s,q){
        k(function t(){
            r.resize(s,q);if(r===h){
                p=s;d=q;j.style.width=p+"px";j.style.height=d+"px";a.style.width=(p*c)+"px";a.style.height=(d*c)+"px";
                if(m.onresize){m.onresize(s,q)
                }
            }
        })
    };

    this.drawImage=function(s,q,u,t){
        k(function r(){
            s.drawImage(q,u,t)
        })
    };
    var f=0;

    this.draw=function(u,q,z,t){
        var s=k(
            function r(){
                u.drawImage(q,z,w)
            },true);
        var v=f++;
        //console.log("image draw task %i - created and blocked",v);
        var w=new Image();
        w.onload=function(){
            s.unblock();
            //console.log("image draw task %i - unblocked",v)
            };
        w.onerror=function(){
            console.log("vnc UNABLE TO LOAD IMAGE",t)
        };
        w.src=t
    };

    this.play=function(s,q,u,r){var t=document.createElement("video");t.type=q;t.src=r;t.addEventListener("play",function(){function v(){s.drawImage(0,0,t);if(!t.ended){window.setTimeout(v,20)}}v()},false);k(t.play)};this.transfer=function(w,r,q,s,B,u,z,v,t){k(function A(){u.transfer(w,r,q,s,B,z,v,t)})};this.put=function(w,r,q,s,A,u,z,v){k(function t(){u.put(w,r,q,s,A,z,v)})};this.copy=function(w,r,q,s,A,t,z,v){k(function u(){t.copy(w,r,q,s,A,z,v)})};this.moveTo=function(s,q,t){k(function r(){s.moveTo(q,t)})};
    this.lineTo=function(s,q,t){k(function r(){s.lineTo(q,t)})};
    this.arc=function(w,r,z,q,v,s,u){k(function t(){w.arc(r,z,q,v,s,u)})};
    this.curveTo=function(v,s,r,u,t,q,z){k(function w(){v.curveTo(s,r,u,t,q,z)})};
    this.close=function(q){k(function r(){q.close()})};
    this.rect=function(s,q,v,r,t){k(function u(){s.rect(q,v,r,t)})};this.clip=function(q){
        k(function r(){q.clip()})};this.strokeColor=function(u,y,s,w,q,t,v,x){k(function z(){u.strokeColor(y,s,w,q,t,v,x)})};
    this.fillColor=function(t,v,u,q,s){k(function w(){t.fillColor(v,u,q,s)})};
    this.strokeLayer=function(u,t,v,s,r){
        k(function q(){
            u.strokeLayer(t,v,s,r)})};this.fillLayer=function(r,q){k(function s(){r.fillLayer(q)})};
    this.push=function(r){k(function q(){r.push()})};this.pop=function(r){k(function q(){r.pop()})};
    this.reset=function(q){k(function r(){q.reset()})};
    this.setTransform=function(s,r,q,x,v,u,t){k(function w(){s.setTransform(r,q,x,v,u,t)})};
    this.transform=function(t,s,q,x,w,v,u){
        k(function r(){t.transform(s,q,x,w,v,u)})};
    this.setChannelMask=function(r,q){
        k(function s(){r.setChannelMask(q)})};
    this.setMiterLimit=function(s,q){
        k(function r(){s.setMiterLimit(q)})};
    this.scale=function(q){
        j.style.transform=j.style.WebkitTransform=j.style.MozTransform=j.style.OTransform=j.style.msTransform="scale("+q+","+q+")";
        c=q;a.style.width=(p*c)+"px";a.style.height=(d*c)+"px"};
    this.getScale=function(){
        return c
    };

    this.flatten=function(){
        var r=document.createElement("canvas");
        r.width=h.width;
        r.height=h.height;
        var s=r.getContext("2d");
        function q(x){var w=[];
            for(var v in x.children){
                w.push(x.children[v])}w.sort(function u(B,A){
                var C=B.z-A.z;if(C!==0){return C}
                var z=B.getElement();
                var D=A.getElement();var y=D.compareDocumentPosition(z);
                if(y&Node.DOCUMENT_POSITION_PRECEDING){return -1}
                if(y&Node.DOCUMENT_POSITION_FOLLOWING){return 1}return 0});
            return w
        }
        function t(A,v,C){
            if(A.width>0&&A.height>0){
                var u=s.globalAlpha;
                s.globalAlpha*=A.alpha/255;
                s.drawImage(A.getCanvas(),v,C);var z=q(A);
                for(var w=0;w<z.length;w++){
                    var B=z[w];t(B,v+B.x,C+B.y)
                }
                s.globalAlpha=u}}t(h,0,0);return r
    }
};

Guacamole.Display.VisibleLayer=function(f,a){
    Guacamole.Layer.apply(this,[f,a]);
    var e=this;
    this.__unique_id=Guacamole.Display.VisibleLayer.__next_id++;
    this.alpha=255;
    this.x=0;
    this.y=0;
    this.z=0;
    this.matrix=[1,0,0,1,0,0];
    this.parent=null;
    this.children={};
    var d=e.getCanvas();
    d.style.position="absolute";
    d.style.left="0px";
    d.style.top="0px";
    var h=document.createElement("div");
    h.appendChild(d);
    h.style.width=f+"px";
    h.style.height=a+"px";
    h.style.position="absolute";
    h.style.left="0px";
    h.style.top="0px";
    h.style.overflow="hidden";
    var c=this.resize;
    this.resize=function(j,i){
        h.style.width=j+"px";
        h.style.height=i+"px";
        c(j,i)
    };
    this.getElement=function(){
        return h
    };
    var g="translate(0px, 0px)";
    var b="matrix(1, 0, 0, 1, 0, 0)";
    this.translate=function(i,j){
        e.x=i;
        e.y=j;
        g="translate("+i+"px,"+j+"px)";
        h.style.transform=h.style.WebkitTransform=h.style.MozTransform=h.style.OTransform=h.style.msTransform=g+" "+b
    };

    this.move=function(j,i,m,l){
        if(e.parent!==j){if(e.parent){delete e.parent.children[e.__unique_id]}e.parent=j;j.children[e.__unique_id]=e;var k=j.getElement();k.appendChild(h)}e.translate(i,m);e.z=l;
        h.style.zIndex=l
    };

    this.shade=function(i){e.alpha=i;h.style.opacity=i/255};this.dispose=function(){if(e.parent){delete e.parent.children[e.__unique_id];e.parent=null}if(h.parentNode){h.parentNode.removeChild(h)}};this.distort=function(j,i,n,m,l,k){e.matrix=[j,i,n,m,l,k];b="matrix("+j+","+i+","+n+","+m+","+l+","+k+")";h.style.transform=h.style.WebkitTransform=h.style.MozTransform=h.style.OTransform=h.style.msTransform=g+" "+b}};Guacamole.Display.VisibleLayer.__next_id=0;var Guacamole=Guacamole||{};Guacamole.InputStream=function(a,b){var c=this;this.index=b;this.onblob=null;this.onend=null;this.sendAck=function(e,d){a.sendAck(c.index,e,d)}};var Guacamole=Guacamole||{};Guacamole.IntegerPool=function(){var b=this;var a=[];this.next_int=0;this.next=function(){if(a.length>0){return a.shift()}return b.next_int++};this.free=function(c){a.push(c)}};var Guacamole=Guacamole||{};Guacamole.Keyboard=function(b){var f=this;this.onkeydown=null;this.onkeyup=null;var g={8:[65288],9:[65289],13:[65293],16:[65505,65505,65506],17:[65507,65507,65508],18:[65513,65513,65514],19:[65299],20:[65509],27:[65307],32:[32],33:[65365],34:[65366],35:[65367],36:[65360],37:[65361],38:[65362],39:[65363],40:[65364],45:[65379],46:[65535],91:[65515],92:[65383],93:null,112:[65470],113:[65471],114:[65472],115:[65473],116:[65474],117:[65475],118:[65476],119:[65477],120:[65478],121:[65479],122:[65480],123:[65481],144:[65407],145:[65300]};var d={Again:[65382],AllCandidates:[65341],Alphanumeric:[65328],Alt:[65513,65513,65514],Attn:[64782],AltGraph:[65514],ArrowDown:[65364],ArrowLeft:[65361],ArrowRight:[65363],ArrowUp:[65362],Backspace:[65288],CapsLock:[65509],Cancel:[65385],Clear:[65291],Convert:[65313],Copy:[64789],Crsel:[64796],CrSel:[64796],CodeInput:[65335],Compose:[65312],Control:[65507,65507,65508],ContextMenu:[65383],Delete:[65535],Down:[65364],End:[65367],Enter:[65293],EraseEof:[64774],Escape:[65307],Execute:[65378],Exsel:[64797],ExSel:[64797],F1:[65470],F2:[65471],F3:[65472],F4:[65473],F5:[65474],F6:[65475],F7:[65476],F8:[65477],F9:[65478],F10:[65479],F11:[65480],F12:[65481],F13:[65482],F14:[65483],F15:[65484],F16:[65485],F17:[65486],F18:[65487],F19:[65488],F20:[65489],F21:[65490],F22:[65491],F23:[65492],F24:[65493],Find:[65384],GroupFirst:[65036],GroupLast:[65038],GroupNext:[65032],GroupPrevious:[65034],FullWidth:null,HalfWidth:null,HangulMode:[65329],Hankaku:[65321],HanjaMode:[65332],Help:[65386],Hiragana:[65317],HiraganaKatakana:[65319],Home:[65360],Hyper:[65517,65517,65518],Insert:[65379],JapaneseHiragana:[65317],JapaneseKatakana:[65318],JapaneseRomaji:[65316],JunjaMode:[65336],KanaMode:[65325],KanjiMode:[65313],Katakana:[65318],Left:[65361],Meta:[65511],ModeChange:[65406],NumLock:[65407],PageDown:[65365],PageUp:[65366],Pause:[65299],Play:[64790],PreviousCandidate:[65342],PrintScreen:[64797],Redo:[65382],Right:[65363],RomanCharacters:null,Scroll:[65300],Select:[65376],Separator:[65452],Shift:[65505,65505,65506],SingleCandidate:[65340],Super:[65515,65515,65516],Tab:[65289],Up:[65362],Undo:[65381],Win:[65515],Zenkaku:[65320],ZenkakuHankaku:[65322]};var o={18:[65511,65511,65514]};var n={65505:true,65506:true,65507:true,65508:true,65511:true,65512:true,65513:true,65514:true,65515:true,65516:true};this.modifiers=new Guacamole.Keyboard.ModifierState();this.pressed={};var j={};var r=[];var c=null;var k=null;function h(t,s){if(!t){return null}return t[s]||t[0]}function q(x,u,t){var w;var s=u.indexOf("U+");if(s>=0){var v=u.substring(s+2);w=String.fromCharCode(parseInt(v,16))}else{if(u.length===1){w=u}else{return h(d[u],t)}}
    if(x){
        w=w.toUpperCase()
    }else{
        w=w.toLowerCase()
    }
    var y=w.charCodeAt(0);
    return m(y)
}
    function l(s){
        return s<=31||(s>=127&&s<=159)
    }
    function m(s){
        if(l(s)){return 65280|s}
        if(s>=0&&s<=255){
            return s
        }
        if(s>=256&&s<=1114111){
            return 16777216|s
        }
        return null
    }
    function e(u,s){
        var t;
        if(!f.modifiers.shift){t=g[u]
        }else{
            t=o[u]||g[u]}
        return h(t,s)
    }

    function i(t){
        if(t===null){
            return
        }
        if(!f.pressed[t]){
            f.pressed[t]=true;if(f.onkeydown){
                var s=f.onkeydown(t);j[t]=s;
                window.clearTimeout(c);
                window.clearInterval(k);
                if(!n[t]){c=window.setTimeout(function(){
                    k=window.setInterval(function(){f.onkeyup(t);
                        f.onkeydown(t)},50)},500)}return s}}return j[t]||false}
    function p(s){if(f.pressed[s]){
        delete f.pressed[s];
        window.clearTimeout(c);
        window.clearInterval(k);
        if(s!==null&&f.onkeyup){f.onkeyup(s)
        }
    }
    }
    function a(t){
        var s=Guacamole.Keyboard.ModifierState.fromKeyboardEvent(t);
        if(f.modifiers.alt&&s.alt===false){
            p(65513);p(65514)}
        if(f.modifiers.shift&&s.shift===false){
            p(65505);p(65506)}if(f.modifiers.ctrl&&s.ctrl===false)
        {p(65507);p(65508)}if(f.modifiers.meta&&s.meta===false){p(65511);
            p(65512)}
        if(
            f.modifiers.hyper&&s.hyper===false){p(65515);p(65516)}
        f.modifiers=s}
    b.addEventListener("keydown",function(v){
        if(!f.onkeydown){
            return
        }
        var w;
        if(window.event){
            w=window.event.keyCode}else{if(v.which){w=v.which}}var s=v.location||v.keyLocation||0;if(!w){v.preventDefault();return}a(v);if(w===229){return}var u=e(w,s);if(v.key){u=u||q(f.modifiers.shift,v.key,s)}else{var t=f.modifiers.ctrl||f.modifiers.alt||f.modifiers.meta||f.modifiers.hyper;if(t&&v.keyIdentifier){u=u||q(f.modifiers.shift,v.keyIdentifier,s)}}if(u!==null){r[w]=u;if(!i(u)){v.preventDefault()}if(f.modifiers.meta&&u!==65511&&u!==65512){p(u)}}},true);b.addEventListener("keypress",function(t){if(!f.onkeydown&&!f.onkeyup){return}var u;if(window.event){u=window.event.keyCode}else{if(t.which){u=t.which}}var s=m(u);a(t);if(!l(u)&&f.modifiers.ctrl&&f.modifiers.alt){p(65507);p(65508);p(65513);p(65514)}if(s!==null){if(!i(s)){t.preventDefault()}p(s)}else{t.preventDefault()}},true);b.addEventListener("keyup",function(t){if(!f.onkeyup){return}t.preventDefault();var u;if(window.event){u=window.event.keyCode}else{if(t.which){u=t.which}}a(t);var s=r[u];if(s!==null){p(s)}r[u]=null},true)};Guacamole.Keyboard.ModifierState=function(){this.shift=false;this.ctrl=false;this.alt=false;this.meta=false;this.hyper=false};Guacamole.Keyboard.ModifierState.fromKeyboardEvent=function(b){var a=new Guacamole.Keyboard.ModifierState();a.shift=b.shiftKey;a.ctrl=b.ctrlKey;a.alt=b.altKey;a.meta=b.metaKey;if(b.getModifierState){a.hyper=b.getModifierState("OS")||b.getModifierState("Super")||b.getModifierState("Hyper")||b.getModifierState("Win")}return a};var Guacamole=Guacamole||{};Guacamole.Layer=function(a,j){var g=this;
    var d=document.createElement("canvas");
    var b=d.getContext("2d");
    b.save();
    var e=true;
    var i=0;
    var h={1:"destination-in",2:"destination-out",4:"source-in",6:"source-atop",8:"source-out",9:"destination-atop",10:"xor",11:"destination-over",12:"copy",14:"source-over",15:"lighter"};function c(n,m){var o=null;if(g.width!==0&&g.height!==0){o=document.createElement("canvas");o.width=g.width;o.height=g.height;var l=o.getContext("2d");l.drawImage(d,0,0,g.width,g.height,0,0,g.width,g.height)}var k=b.globalCompositeOperation;d.width=n;d.height=m;
    if(o){
        b.drawImage(o,0,0,g.width,g.height,0,0,g.width,g.height)}
        b.globalCompositeOperation=k;
    g.width=n;
    g.height=m;i=0;b.save()
    }

    function f(k,r,l,m){
        var p=l+k;
        var o=m+r;
        var n;
        if(p>g.width){
            n=p}else{n=g.width}var q;if(o>g.height){q=o}else{q=g.height}g.resize(n,q)}this.autosize=false;this.width=a;this.height=j;this.getCanvas=function(){return d};this.resize=function(l,k){if(l!==g.width||k!==g.height){c(l,k)}};this.drawImage=function(k,m,l){if(g.autosize){f(k,m,l.width,l.height)}b.drawImage(l,k,m)};this.transfer=function(u,n,l,o,z,v,t,q){var p=u.getCanvas();if(n>=p.width||l>=p.height){return}if(n+o>p.width){o=p.width-n}if(l+z>p.height){z=p.height-l}if(o===0||z===0){return}if(g.autosize){f(v,t,o,z)}var k=u.getCanvas().getContext("2d").getImageData(n,l,o,z);var s=b.getImageData(v,t,o,z);for(var r=0;r<o*z*4;r+=4){var m=new Guacamole.Layer.Pixel(k.data[r],k.data[r+1],k.data[r+2],k.data[r+3]);var w=new Guacamole.Layer.Pixel(s.data[r],s.data[r+1],s.data[r+2],s.data[r+3]);q(m,w);s.data[r]=w.red;s.data[r+1]=w.green;s.data[r+2]=w.blue;s.data[r+3]=w.alpha}b.putImageData(s,v,t)};this.put=function(q,m,l,n,s,r,p){var o=q.getCanvas();if(m>=o.width||l>=o.height){return}if(m+n>o.width){n=o.width-m}if(l+s>o.height){s=o.height-l}if(n===0||s===0){return}if(g.autosize){f(r,p,n,s)}var k=q.getCanvas().getContext("2d").getImageData(m,l,n,s);b.putImageData(k,r,p)};this.copy=function(o,m,l,n,q,k,r){var p=o.getCanvas();if(m>=p.width||l>=p.height){return}if(m+n>p.width){n=p.width-m}if(l+q>p.height){q=p.height-l}if(n===0||q===0){return}if(g.autosize){f(k,r,n,q)}b.drawImage(p,m,l,n,q,k,r,n,q)};this.moveTo=function(k,l){if(e){b.beginPath();e=false}if(g.autosize){f(k,l,0,0)}b.moveTo(k,l)};this.lineTo=function(k,l){if(e){b.beginPath();e=false}if(g.autosize){f(k,l,0,0)}b.lineTo(k,l)};this.arc=function(l,p,k,o,m,n){if(e){b.beginPath();e=false}if(g.autosize){f(l,p,0,0)}b.arc(l,p,k,o,m,n)};this.curveTo=function(m,l,o,n,k,p){if(e){b.beginPath();e=false}if(g.autosize){f(k,p,0,0)}b.bezierCurveTo(m,l,o,n,k,p)};this.close=function(){b.closePath();e=true};this.rect=function(k,n,l,m){if(e){b.beginPath();e=false}if(g.autosize){f(k,n,l,m)}b.rect(k,n,l,m)};this.clip=function(){b.clip();e=true};this.strokeColor=function(n,q,m,p,o,k,l){b.lineCap=n;b.lineJoin=q;b.lineWidth=m;b.strokeStyle="rgba("+p+","+o+","+k+","+l/255+")";b.stroke();e=true};this.fillColor=function(n,m,k,l){b.fillStyle="rgba("+n+","+m+","+k+","+l/255+")";b.fill();e=true};this.strokeLayer=function(m,n,l,k){b.lineCap=m;b.lineJoin=n;b.lineWidth=l;b.strokeStyle=b.createPattern(k.getCanvas(),"repeat");b.stroke();e=true};this.fillLayer=function(k){b.fillStyle=b.createPattern(k.getCanvas(),"repeat");b.fill();e=true};this.push=function(){b.save();i++};this.pop=function(){if(i>0){b.restore();i--}};this.reset=function(){while(i>0){b.restore();i--}b.restore();b.save();b.beginPath();e=false};this.setTransform=function(l,k,p,o,n,m){b.setTransform(l,k,p,o,n,m)};this.transform=function(l,k,p,o,n,m){b.transform(l,k,p,o,n,m)};this.setChannelMask=function(k){b.globalCompositeOperation=h[k]};this.setMiterLimit=function(k){b.miterLimit=k};d.width=a;d.height=j;
    d.style.zIndex=100};
Guacamole.Layer.ROUT=2;
Guacamole.Layer.ATOP=6;
Guacamole.Layer.XOR=10;
Guacamole.Layer.ROVER=11;
Guacamole.Layer.OVER=14;
Guacamole.Layer.PLUS=15;
Guacamole.Layer.RIN=1;
Guacamole.Layer.IN=4;
Guacamole.Layer.OUT=8;Guacamole.Layer.RATOP=9;Guacamole.Layer.SRC=12;Guacamole.Layer.Pixel=function(f,e,c,d){this.red=f;this.green=e;this.blue=c;this.alpha=d};var Guacamole=Guacamole||{};Guacamole.Mouse=function(e){var d=this;this.touchMouseThreshold=3;
    this.scrollThreshold=120;this.PIXELS_PER_LINE=40;this.PIXELS_PER_PAGE=640;this.currentState=new Guacamole.Mouse.State(0,0,false,false,false,false,false);this.onmousedown=null;this.onmouseup=null;this.onmousemove=null;var f=0;var a=0;function c(h){h.stopPropagation();if(h.preventDefault){h.preventDefault()}h.returnValue=false}e.addEventListener("contextmenu",function(h){c(h)},false);e.addEventListener("mousemove",function(h){c(h);if(f){f--;return}d.currentState.fromClientPosition(e,h.clientX,h.clientY);if(d.onmousemove){d.onmousemove(d.currentState)}},false);e.addEventListener("mousedown",function(h){c(h);if(f){return}switch(h.button){case 0:d.currentState.left=true;break;case 1:d.currentState.middle=true;break;case 2:d.currentState.right=true;break}if(d.onmousedown){d.onmousedown(d.currentState)}},false);e.addEventListener("mouseup",function(h){c(h);if(f){return}switch(h.button){case 0:d.currentState.left=false;break;case 1:d.currentState.middle=false;break;case 2:d.currentState.right=false;break}if(d.onmouseup){d.onmouseup(d.currentState)}},false);e.addEventListener("mouseout",function(i){if(!i){i=window.event}var h=i.relatedTarget||i.toElement;while(h!==null){if(h===e){return}h=h.parentNode}c(i);if(d.currentState.left||d.currentState.middle||d.currentState.right){d.currentState.left=false;d.currentState.middle=false;d.currentState.right=false;if(d.onmouseup){d.onmouseup(d.currentState)}}},false);e.addEventListener("selectstart",function(h){c(h)},false);function b(){f=d.touchMouseThreshold}e.addEventListener("touchmove",b,false);e.addEventListener("touchstart",b,false);e.addEventListener("touchend",b,false);function g(h){var i=h.deltaY||-h.wheelDeltaY||-h.wheelDelta;if(i){if(h.deltaMode===1){i=h.deltaY*d.PIXELS_PER_LINE}else{if(h.deltaMode===2){i=h.deltaY*d.PIXELS_PER_PAGE}}}else{i=h.detail*d.PIXELS_PER_LINE}a+=i;while(a<=-d.scrollThreshold){if(d.onmousedown){d.currentState.up=true;d.onmousedown(d.currentState)}if(d.onmouseup){d.currentState.up=false;d.onmouseup(d.currentState)}a+=d.scrollThreshold}while(a>=d.scrollThreshold){if(d.onmousedown){d.currentState.down=true;d.onmousedown(d.currentState)}if(d.onmouseup){d.currentState.down=false;d.onmouseup(d.currentState)}a-=d.scrollThreshold}c(h)}e.addEventListener("DOMMouseScroll",g,false);e.addEventListener("mousewheel",g,false);e.addEventListener("wheel",g,false)};Guacamole.Mouse.State=function(b,h,f,c,d,a,g){var e=this;this.x=b;this.y=h;this.left=f;this.middle=c;this.right=d;this.up=a;this.down=g;this.fromClientPosition=function(j,n,m){e.x=n-j.offsetLeft;e.y=m-j.offsetTop;var l=j.offsetParent;while(l&&!(l===document.body)){e.x-=l.offsetLeft-l.scrollLeft;e.y-=l.offsetTop-l.scrollTop;l=l.offsetParent}if(l){var k=document.body.scrollLeft||document.documentElement.scrollLeft;var i=document.body.scrollTop||document.documentElement.scrollTop;e.x-=l.offsetLeft-k;e.y-=l.offsetTop-i}}};Guacamole.Mouse.Touchpad=function(d){var c=this;this.scrollThreshold=20*(window.devicePixelRatio||1);this.clickTimingThreshold=250;this.clickMoveThreshold=10*(window.devicePixelRatio||1);this.currentState=new Guacamole.Mouse.State(0,0,false,false,false,false,false);this.onmousedown=null;this.onmouseup=null;this.onmousemove=null;var a=0;var h=0;var g=0;var f=0;var e=0;var b={1:"left",2:"right",3:"middle"};var j=false;var i=null;d.addEventListener("touchend",function(m){m.preventDefault();if(j&&m.touches.length===0){var l=new Date().getTime();var k=b[a];if(c.currentState[k]){c.currentState[k]=false;if(c.onmouseup){c.onmouseup(c.currentState)}if(i){window.clearTimeout(i);i=null}}if(l-f<=c.clickTimingThreshold&&e<c.clickMoveThreshold){c.currentState[k]=true;if(c.onmousedown){c.onmousedown(c.currentState)}i=window.setTimeout(function(){c.currentState[k]=false;if(c.onmouseup){c.onmouseup(c.currentState)}j=false},c.clickTimingThreshold)}if(!i){j=false}}},false);d.addEventListener("touchstart",function(l){l.preventDefault();a=Math.min(l.touches.length,3);if(i){window.clearTimeout(i);i=null}if(!j){j=true;var k=l.touches[0];h=k.clientX;g=k.clientY;f=new Date().getTime();e=0}},false);d.addEventListener("touchmove",function(m){m.preventDefault();var q=m.touches[0];var p=q.clientX-h;var o=q.clientY-g;e+=Math.abs(p)+Math.abs(o);if(a===1){var l=e/(new Date().getTime()-f);var n=1+l;c.currentState.x+=p*n;c.currentState.y+=o*n;if(c.currentState.x<0){c.currentState.x=0}else{if(c.currentState.x>=d.offsetWidth){c.currentState.x=d.offsetWidth-1}}if(c.currentState.y<0){c.currentState.y=0}else{if(c.currentState.y>=d.offsetHeight){c.currentState.y=d.offsetHeight-1}}if(c.onmousemove){c.onmousemove(c.currentState)}h=q.clientX;g=q.clientY}else{if(a===2){if(Math.abs(o)>=c.scrollThreshold){var k;if(o>0){k="down"}else{k="up"}c.currentState[k]=true;if(c.onmousedown){c.onmousedown(c.currentState)}c.currentState[k]=false;if(c.onmouseup){c.onmouseup(c.currentState)}h=q.clientX;g=q.clientY}}}},false)};Guacamole.Mouse.Touchscreen=function(e){var d=this;var k=false;var j=null;var i=null;var l=null;var h=null;this.scrollThreshold=20*(window.devicePixelRatio||1);this.clickTimingThreshold=250;this.clickMoveThreshold=16*(window.devicePixelRatio||1);this.longPressThreshold=500;this.currentState=new Guacamole.Mouse.State(0,0,false,false,false,false,false);this.onmousedown=null;this.onmouseup=null;this.onmousemove=null;function m(o){if(!d.currentState[o]){d.currentState[o]=true;if(d.onmousedown){d.onmousedown(d.currentState)}}}function g(o){if(d.currentState[o]){d.currentState[o]=false;if(d.onmouseup){d.onmouseup(d.currentState)}}}function c(o){m(o);g(o)}function b(o,p){d.currentState.fromClientPosition(e,o,p);if(d.onmousemove){d.onmousemove(d.currentState)}}function a(o){var r=o.touches[0]||o.changedTouches[0];var q=r.clientX-j;var p=r.clientY-i;return Math.sqrt(q*q+p*p)>=d.clickMoveThreshold}function f(o){var p=o.touches[0];k=true;j=p.clientX;i=p.clientY}function n(){window.clearTimeout(l);window.clearTimeout(h);k=false}e.addEventListener("touchend",function(o){if(!k){return}if(o.touches.length!==0||o.changedTouches.length!==1){n();return}window.clearTimeout(h);g("left");if(!a(o)){o.preventDefault();if(!d.currentState.left){var p=o.changedTouches[0];b(p.clientX,p.clientY);m("left");l=window.setTimeout(function(){g("left");n()},d.clickTimingThreshold)}}},false);e.addEventListener("touchstart",function(o){if(o.touches.length!==1){n();return}o.preventDefault();f(o);window.clearTimeout(l);h=window.setTimeout(function(){var p=o.touches[0];b(p.clientX,p.clientY);c("right");n()},d.longPressThreshold)},false);e.addEventListener("touchmove",function(o){if(!k){return}if(a(o)){window.clearTimeout(h)}if(o.touches.length!==1){n();return}if(d.currentState.left){o.preventDefault();var p=o.touches[0];b(p.clientX,p.clientY)}},false)};var Guacamole=Guacamole||{};Guacamole.OnScreenKeyboard=function(f){var u=this;var a=0;var i={};var c=[];var l={};var e=1;var m;var o;this.touchMouseThreshold=3;var q=0;function h(){q=u.touchMouseThreshold}if(Node.classList){m=function(v,w){v.classList.add(w)};o=function(v,w){v.classList.remove(w)}}else{m=function(v,w){v.className+=" "+w};o=function(v,w){v.className=v.className.replace(/([^ ]+)[ ]*/g,function(A,x,y,B,z){if(x==w){return""}return A})}}function p(v){var w=l[v];if(!w){w=e;e<<=1;l[v]=w}return w}function t(w,x,v,y){this.width=x;this.height=v;this.scale=function(z){w.style.width=(x*z)+"px";w.style.height=(v*z)+"px";if(y){w.style.lineHeight=(v*z)+"px";w.style.fontSize=z+"px"}}}function g(x,z){var w=x.childNodes;for(var v=0;v<w.length;v++){var A=w[v];if(!A.tagName){continue}var y=z[A.tagName];if(y){y(A)}else{throw new Error("Unexpected "+A.tagName+" within "+x.tagName)}}}var b=document.createElement("div");b.className="guac-keyboard";var n=new XMLHttpRequest();n.open("GET",f,false);n.send(null);var d=n.responseXML;if(d){function j(w){var x=document.createElement("div");x.className="guac-keyboard-row";g(w,{column:function(z){x.appendChild(s(z))},gap:function y(A){var B=document.createElement("div");B.className="guac-keyboard-gap";var z=1;if(A.getAttribute("size")){z=parseFloat(A.getAttribute("size"))}c.push(new t(B,z,z));x.appendChild(B)},key:function v(F){var z=document.createElement("div");z.className="guac-keyboard-key";if(F.getAttribute("class")){z.className+=" "+F.getAttribute("class")}var I=document.createElement("div");I.className="guac-keyboard-key-container";I.appendChild(z);var K=new Guacamole.OnScreenKeyboard.Key();var G=1;if(F.getAttribute("size")){G=parseFloat(F.getAttribute("size"))}K.size=G;g(F,{cap:function H(P){var Q=P.textContent||P.text;if(Q.length==0){Q=" "}var O=null;if(P.getAttribute("keysym")){O=parseInt(P.getAttribute("keysym"))}else{if(Q.length==1){var T=Q.charCodeAt(0);if(T>=0&&T<=255){O=T}else{if(T>=256&&T<=1114111){O=16777216|T}}}}var S=new Guacamole.OnScreenKeyboard.Cap(Q,O);if(P.getAttribute("modifier")){S.modifier=P.getAttribute("modifier")}var R=document.createElement("div");R.className="guac-keyboard-cap";R.textContent=Q;z.appendChild(R);if(P.getAttribute("class")){R.className+=" "+P.getAttribute("class")}var L=0;if(P.getAttribute("if")){var M=P.getAttribute("if").split(",");for(var N=0;N<M.length;N++){L|=p(M[N]);m(R,"guac-keyboard-requires-"+M[N]);m(z,"guac-keyboard-uses-"+M[N])}}K.modifierMask|=L;K.caps[L]=S}});c.push(new t(I,G,1,true));x.appendChild(I);function C(){if(!K.pressed){m(z,"guac-keyboard-pressed");var M=K.getCap(a);if(M.modifier){var L="guac-keyboard-modifier-"+M.modifier;var N=p(M.modifier);a^=N;if(a&N){m(b,L);i[M.modifier]=M.keysym;if(u.onkeydown&&M.keysym){u.onkeydown(M.keysym)}}else{var O=i[M.modifier];o(b,L);delete i[M.modifier];if(u.onkeyup&&O){u.onkeyup(O)}}}else{if(u.onkeydown&&M.keysym){u.onkeydown(M.keysym)}}K.pressed=true}}function J(){if(K.pressed){var L=K.getCap(a);o(z,"guac-keyboard-pressed");if(!L.modifier&&u.onkeyup&&L.keysym){u.onkeyup(L.keysym)}K.pressed=false}}function E(L){L.preventDefault();q=u.touchMouseThreshold;C()}function A(L){L.preventDefault();q=u.touchMouseThreshold;J()}function B(L){L.preventDefault();if(q==0){C()}}function D(L){L.preventDefault();if(q==0){J()}}z.addEventListener("touchstart",E,true);z.addEventListener("touchend",A,true);z.addEventListener("mousedown",B,true);z.addEventListener("mouseup",D,true);z.addEventListener("mouseout",D,true)}});return x}function s(w){var v=document.createElement("div");v.className="guac-keyboard-column";if(v.getAttribute("align")){v.style.textAlign=v.getAttribute("align")}g(w,{row:function(x){v.appendChild(j(x))}});return v}var r=d.documentElement;if(r.tagName!="keyboard"){throw new Error("Root element must be keyboard")}if(!r.getAttribute("size")){throw new Error("size attribute is required for keyboard")}var k=parseFloat(r.getAttribute("size"));g(r,{row:function(v){b.appendChild(j(v))},column:function(v){b.appendChild(s(v))}})}b.onselectstart=b.onmousemove=b.onmouseup=b.onmousedown=function(v){if(q){q--}v.stopPropagation();return false};this.onkeydown=null;this.onkeyup=null;this.getElement=function(){return b};this.resize=function(x){var y=Math.floor(x*10/k)/10;for(var w=0;w<c.length;w++){var v=c[w];v.scale(y)}}};Guacamole.OnScreenKeyboard.Key=function(){var a=this;this.pressed=false;this.size=1;this.caps={};this.modifierMask=0;this.getCap=function(b){return a.caps[b&a.modifierMask]}};Guacamole.OnScreenKeyboard.Cap=function(c,b,a){this.modifier=null;this.text=c;this.keysym=b;if(a){this.modifier=a}};var Guacamole=Guacamole||{};Guacamole.OutputStream=function(a,b){var c=this;this.index=b;this.onack=null;this.sendBlob=function(d){a.sendBlob(c.index,d)};this.sendEnd=function(){a.endStream(c.index)}};var Guacamole=Guacamole||{};Guacamole.Parser=function(){var e=this;var b="";var d=[];var c=-1;var a=0;this.receive=function(k){if(a>4096&&c>=a){b=b.substring(a);c-=a;a=0}b+=k;while(c<b.length){if(c>=a){var h=b.substring(a,c);var g=b.substring(c,c+1);d.push(h);if(g==";"){var j=d.shift();if(e.oninstruction!=null){e.oninstruction(j,d)}d.length=0}else{if(g!=","){throw new Error("Illegal terminator.")}}a=c+1}var f=b.indexOf(".",a);if(f!=-1){var i=parseInt(b.substring(c+1,f));if(i==NaN){throw new Error("Non-numeric character in element length.")}a=f+1;c=a+i}else{a=b.length;break}}};this.oninstruction=null};var Guacamole=Guacamole||{};Guacamole.Status=function(b,a){var c=this;this.code=b;this.message=a;this.isError=function(){return c.code<0||c.code>255}};Guacamole.Status.Code={SUCCESS:0,UNSUPPORTED:256,SERVER_ERROR:512,SERVER_BUSY:513,UPSTREAM_TIMEOUT:514,UPSTREAM_ERROR:515,RESOURCE_NOT_FOUND:516,RESOURCE_CONFLICT:517,CLIENT_BAD_REQUEST:768,CLIENT_UNAUTHORIZED:769,CLIENT_FORBIDDEN:771,CLIENT_TIMEOUT:776,CLIENT_OVERRUN:781,CLIENT_BAD_TYPE:783,CLIENT_TOO_MANY:797};var Guacamole=Guacamole||{};Guacamole.StringReader=function(e){var d=this;var b=new Guacamole.ArrayBufferReader(e);var f=0;var c=0;function a(h){var l="";var g=new Uint8Array(h);for(var j=0;j<g.length;j++){var k=g[j];if(f===0){if((k|127)===127){l+=String.fromCharCode(k)}else{if((k|31)===223){c=k&31;f=1}else{if((k|15)===239){c=k&15;f=2}else{if((k|7)===247){c=k&7;f=3}else{l+="\uFFFD"}}}}}else{if((k|63)===191){c=(c<<6)|(k&63);f--;if(f===0){l+=String.fromCharCode(c)}}else{f=0;l+="\uFFFD"}}}return l}b.ondata=function(g){var h=a(g);if(d.ontext){d.ontext(h)}};b.onend=function(){if(d.onend){d.onend()}};this.ontext=null;this.onend=null};var Guacamole=Guacamole||{};Guacamole.StringWriter=function(h){var e=this;var c=new Guacamole.ArrayBufferWriter(h);var a=new Uint8Array(8192);var g=0;c.onack=function(i){if(e.onack){e.onack(i)}};function f(i){if(g+i>=a.length){var j=new Uint8Array((g+i)*2);j.set(a);a=j}g+=i}function d(m){var k;var j;if(m<=127){k=0;j=1}else{if(m<=2047){k=192;j=2}else{if(m<=65535){k=224;j=3}else{if(m<=2097151){k=240;j=4}else{d(65533);return}}}}f(j);var n=g-1;for(var l=1;l<j;l++){a[n--]=128|(m&63);m>>=6}a[n]=k|m}function b(m){for(var j=0;j<m.length;j++){var l=m.charCodeAt(j);d(l)}if(g>0){var k=a.subarray(0,g);g=0;return k}}this.sendText=function(i){c.sendData(b(i))};this.sendEnd=function(){c.sendEnd()};this.onack=null};var Guacamole=Guacamole||{};

Guacamole.Tunnel=function(){
    this.connect=function(a){};
    this.disconnect=function(){};
    this.sendMessage=function(a){};
    this.state=Guacamole.Tunnel.State.CONNECTING;
    this.receiveTimeout=15000;
    this.onerror=null;
    this.onstatechange=null;
    this.oninstruction=null
};

Guacamole.Tunnel.State={CONNECTING:0,OPEN:1,CLOSED:2};

Guacamole.HTTPTunnel=function(f, port){
    var p=this;
    var i;
    var h=f+"?connect:" +"port="+ port;
    var s=f+"?read:";
    var j=f+"?write:";
    var g=1;
    var k=0;
    var q=g;
    var n=false;
    var e="";
    var c=null;

    function b(){
        window.clearTimeout(c);
        c=window.setTimeout(function(){
                setStatus(new Guacamole.Status(
                    Guacamole.Status.Code.UPSTREAM_TIMEOUT,"Server timeout."))
            },
            p.receiveTimeout)
    }

    function setStatus(t){
        if(p.state===Guacamole.Tunnel.State.CLOSED)
        {
            return
        }
        if(t.code!==Guacamole.Status.Code.SUCCESS&&p.onerror){
            if(p.state==Guacamole.Tunnel.State.CONNECTING||t.code!==
                Guacamole.Status.Code.RESOURCE_NOT_FOUND){
                p.onerror(t)
            }
        }
        p.state=Guacamole.Tunnel.State.CLOSED;
        if(p.onstatechange){
            p.onstatechange(p.state)
        }
    }

    this.sendMessage=function(){
        if(p.state!==Guacamole.Tunnel.State.OPEN){
            return
        }
        if(arguments.length===0){
            return
        }
        function u(x){
            var w=new String(x);
            return w.length+"."+w
        }
        var v=u(arguments[0]);
        for(var t=1;t<arguments.length;t++){
            v+=","+u(arguments[t])
        }
        v+=";";
        e+=v;
        if(!n){
            m()
        }
    };

    function m(){
        if(p.state!==Guacamole.Tunnel.State.OPEN){
            return
        }
        if(e.length>0){
            n=true;
            var t=new XMLHttpRequest();
            t.open("POST",j+i);
            t.setRequestHeader("Content-type","application/x-www-form-urlencoded; charset=UTF-8");
            t.onreadystatechange=function(){
                if(t.readyState===4){
                    if(t.status!==200){
                        l(t)
                    }else{m()
                    }
                }
            };
            t.send(e);e=""
        }else{
            n=false
        }
    }

    function l(v){
        var u=parseInt(v.getResponseHeader("Guacamole-Status-Code"));
        var t=v.getResponseHeader("Guacamole-Error-Message");
        setStatus(new Guacamole.Status(u,t))
    }

    function r(w){
        var t=null;
        var y=null;
        var v=0;
        var u=-1;
        var z=0;
        var x=new Array();
        function A(){
            if(p.state!==Guacamole.Tunnel.State.OPEN){
                if(t!==null){
                    clearInterval(t)
                }
                return
            }
            if(w.readyState<2){
                return
            }
            var B;
            try{
                B=w.status
            }catch(I){
                B=200
            }
            if(!y&&B===200){
                y=o()
            }
            if(w.readyState===3||w.readyState===4){
                b();
                if(q===g){
                    if(w.readyState===3&&!t)
                    {
                        t=setInterval(A,30)
                    }else{
                        if(w.readyState===4&&!t){
                            clearInterval(t)
                        }
                    }
                }
                if(w.status===0){
                    p.disconnect();
                    return
                }else{
                    if(w.status!==200){
                        l(w);
                        return
                    }
                }
                var H;
                try{
                    H=w.responseText
                }catch(I){
                    return
                }
                while(u<H.length){
                    if(u>=z){
                        var D=H.substring(z,u);
                        var C=H.substring(u,u+1);
                        x.push(D);
                        if(C===";"){
                            var G=x.shift();
                            if(p.oninstruction){
                                p.oninstruction(G,x)
                            }
                            x.length=0
                        }
                        z=u+1
                    }
                    var E=H.indexOf(".",z);
                    if(E!==-1){
                        var F=parseInt(H.substring(u+1,E));if(F===0){
                            if(!t){
                                clearInterval(t)
                            }
                            w.onreadystatechange=null;w.abort();
                            if(y){
                                r(y)
                            }
                            break
                        }
                        z=E+1;
                        u=z+F
                    }else{
                        z=H.length;break
                    }
                }
            }
        }

        if(q===g){
            w.onreadystatechange=function(){
                if(w.readyState===3){
                    v++;if(v>=2){
                        q=k;
                        w.onreadystatechange=A
                    }
                }
                A()
            }
        }else{
            w.onreadystatechange=A}A()
    }
    var d=0;
    function o()
        {
            var t=new XMLHttpRequest();
            t.open("GET",s+i+":"+(d++));
            t.send(null);
            return t
        }

    this.connect=function(t){
        b();
        var u=new XMLHttpRequest();
        u.onreadystatechange=function(){
            if(u.readyState!==4){
                console.log("vnc u.readyState!==4");
                return
            }
            if(u.status!==200){
                l(u);
                console.log("vnc u.status!==200");
                return
            }
            b();
            i=u.responseText;
            p.state=Guacamole.Tunnel.State.OPEN;
            console.log("vnc tunnel open");
            if(p.onstatechange){
                p.onstatechange(p.state)
            }
            r(o())
        };

        u.open("POST",h,true);
        u.setRequestHeader("Content-type","application/x-www-form-urlencoded; charset=UTF-8");
        u.send(t)
    };

    this.disconnect=function(){
        setStatus(new Guacamole.Status(Guacamole.Status.Code.SUCCESS,"Manually closed."))
    }
};

Guacamole.HTTPTunnel.prototype=new Guacamole.Tunnel();

Guacamole.WebSocketTunnel=function(c){
    var e=this;
    var f=null;
    var a=null;
    var b={"http:":"ws:","https:":"wss:"};
    if(c.substring(0,3)!=="ws:"&&c.substring(0,4)!=="wss:"){
        var h=b[window.location.protocol];if(c.substring(0,1)==="/"){
            c=h+"//"+window.location.host+c
        }else{
            var d=window.location.pathname.lastIndexOf("/");
            var j=window.location.pathname.substring(0,d+1);
            c=h+"//"+window.location.host+j+c
        }
    }
    function i(){
        window.clearTimeout(a);
        a=window.setTimeout(function(){
            g(new Guacamole.Status(Guacamole.Status.Code.UPSTREAM_TIMEOUT,"Server timeout."))
        },e.receiveTimeout)
    }
    function g(k){
        if(e.state===Guacamole.Tunnel.State.CLOSED){
            return
        }
        if(k.code!==Guacamole.Status.Code.SUCCESS&&e.onerror){
            e.onerror(k)
        }
        e.state=Guacamole.Tunnel.State.CLOSED;
        if(e.onstatechange){
            e.onstatechange(e.state)
        }
        f.close()
    }
    this.sendMessage=function(n){
        if(e.state!==Guacamole.Tunnel.State.OPEN){
            return
        }

        if(arguments.length===0){
            return
        }

        function l(p){
            var o=new String(p);
            return o.length+"."+o
        }
        var m=l(arguments[0]);
        for(var k=1;k<arguments.length;k++){
            m+=","+l(arguments[k])}m+=";";
        f.send(m)
    };
    this.connect=function(k){
        i();
        f=new WebSocket(c+"?"+k,"guacamole");

        f.onopen=function(l){i();
            e.state=Guacamole.Tunnel.State.OPEN;
            if(e.onstatechange){
                e.onstatechange(e.state)
            }
        };

        f.onclose=function(l){
            g(new Guacamole.Status(parseInt(l.reason),l.reason))};

        f.onerror=function(l){
            g(new Guacamole.Status(Guacamole.Status.Code.SERVER_ERROR,l.data))
        };

        f.onmessage=function(n){
            i();
            var u=n.data;
            var s=0;var r;var m=[];
            do{
                var t=u.indexOf(".",s);
                if(t!==-1){
                    var o=parseInt(u.substring(r+1,t));
                    s=t+1;
                    r=s+o
                }else{
                    g(new Guacamole.Status(Guacamole.Status.Code.SERVER_ERROR,"Incomplete instruction."))
                }
                var q=u.substring(s,r);
                var l=u.substring(r,r+1);m.push(q);
                if(l===";"){
                    var p=m.shift();
                    if(e.oninstruction){
                        e.oninstruction(p,m)
                    }
                    m.length=0}s=r+1}while(s<u.length)}};
    this.disconnect=function(){
        g(new Guacamole.Status(Guacamole.Status.Code.SUCCESS,"Manually closed."))
    }
};
Guacamole.WebSocketTunnel.prototype=new Guacamole.Tunnel();
Guacamole.ChainedTunnel=function(e){
    var c=this;var a;
    var f=[];
    for(var d=0;d<arguments.length;d++){
        f.push(arguments[d])}function b(i){
        c.disconnect=i.disconnect;
        c.sendMessage=i.sendMessage;function h(){
            var j=f.shift();if(j){
                i.onerror=null;
                i.oninstruction=null;
                i.onstatechange=null;
                b(j)
            }
            return j
        }
        function g(){
            i.onstatechange=c.onstatechange;i.oninstruction=c.oninstruction;i.onerror=c.onerror
        }
        i.onstatechange=function(j){
            switch(j){
                case Guacamole.Tunnel.State.OPEN:
                    g();
                    if(c.onstatechange){
                        c.onstatechange(j)
                    }
                    break;
                case Guacamole.Tunnel.State.CLOSED:
                    if(!h()&&c.onstatechange){
                        c.onstatechange(j)
                    }
                    break
            }
        };
        i.oninstruction=function(k,j){
            g();
            if(c.oninstruction){
                c.oninstruction(k,j)
            }

        };
        i.onerror=function(j){
            if(!h()&&c.onerror){
                c.onerror(j)
            }
        };
        i.connect(a)
    }
    this.connect=function(h){
        a=h;var g=f.shift();
        if(g){b(g)}else{if(c.onerror){
            c.onerror(Guacamole.Status.Code.SERVER_ERROR,"No tunnels to try.")
        }
        }
    }
};

Guacamole.ChainedTunnel.prototype=new Guacamole.Tunnel();
var Guacamole=Guacamole||{};
Guacamole.API_VERSION="0.9.2";





