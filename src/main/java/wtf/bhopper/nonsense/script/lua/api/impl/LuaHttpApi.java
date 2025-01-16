package wtf.bhopper.nonsense.script.lua.api.impl;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.script.lua.api.LuaApi;
import wtf.bhopper.nonsense.util.misc.Http;

public class LuaHttpApi extends LuaApi {

    public LuaHttpApi() {
        this.addFunc("get", args -> {
            if (!Nonsense.getScriptManager().getLuaEnv().allowHttp()) {
                return NIL;
            }

            String url = args.arg(1).checkjstring();

            try {
                return valueOf(new Http(url)
                        .header("User-Agent", Nonsense.NAME)
                        .get()
                        .body());
            } catch (Exception _) {
                return NIL;
            }
        });

        this.addFunc("get_async", args -> {
            if (!Nonsense.getScriptManager().getLuaEnv().allowHttp()) {
                return NIL;
            }

            String url = args.arg(1).checkjstring();
            LuaTable method = args.arg(2).checktable();

            new Thread(() -> {
                try {
                    String text = new Http(url)
                            .header("User-Agent", Nonsense.NAME)
                            .get()
                            .body();
                    method.get("run").call(text);
                } catch (Exception _) {}
            }).start();

            return NIL;
        });

        this.addFunc("post", args -> {
            if (!Nonsense.getScriptManager().getLuaEnv().allowHttp()) {
                return NIL;
            }

            String url = args.arg(1).checkjstring();

            try {
                return valueOf(new Http(url)
                        .header("User-Agent", Nonsense.NAME)
                        .post()
                        .body());
            } catch (Exception _) {
                return NIL;
            }
        });

        this.addFunc("post_async", args -> {
            if (!Nonsense.getScriptManager().getLuaEnv().allowHttp()) {
                return NIL;
            }

            String url = args.arg(1).checkjstring();
            LuaTable method = args.arg(2).checktable();

            new Thread(() -> {
                try {
                    String text = new Http(url)
                            .header("User-Agent", Nonsense.NAME)
                            .post()
                            .body();
                    method.get("run").call(text);
                } catch (Exception _) {}
            }).start();

            return NIL;
        });

        this.addFunc("load", args -> {
            if (!Nonsense.getScriptManager().getLuaEnv().allowHttp()) {
                return NIL;
            }

            String url = args.arg(1).checkjstring();

            try {
                String text = new Http(url)
                        .header("User-Agent", Nonsense.NAME)
                        .get()
                        .body();

                LuaValue chunk = Nonsense.getScriptManager().getLuaEnv().getGlobals().load(text);
                Nonsense.getScriptManager().getLuaEnv().runScript(chunk);
            } catch (Exception _) {

            }

            return NIL;
        });

    }

}
