package org.liquidplayer.webkit.javascriptcore;

import static org.junit.Assert.*;

public class JSContextTest {

    public interface JSContextInterface {
        @SuppressWarnings("unused")
        int func1();
    }
    public class JSContextClass extends JSContext implements JSContextInterface {
        public JSContextClass() {
            super(JSContextInterface.class);
        }
        @Override
        public int func1() {
            return 55;
        }
    }
    public class JSContextInGroup extends JSContext implements JSContextInterface {
        public JSContextInGroup(JSContextGroup inGroup) {
            super(inGroup, JSContextInterface.class);
        }
        @Override
        public int func1() {
            return property("testObject").toFunction().call().toNumber().intValue();
        }
    }

    @org.junit.Test
    public void testJSContextConstructor() throws Exception {
        JSContext context = new JSContext();
        context.property("test",10);
        assertTrue(context.property("test").toNumber().equals(10.0));

        JSContext context1 = new JSContextClass();
        JSValue ret = context1.evaluateScript("func1()");
        assertTrue(ret.toNumber().equals(55.0));

        JSContextGroup contextGroup = new JSContextGroup();
        JSContext context2 = new JSContext(contextGroup);
        JSContext context3 = new JSContext(contextGroup);
        context2.evaluateScript("var forty_two = 42; var cx2_func = function() { return forty_two; };");
        JSValue cx2_func = context2.property("cx2_func");
        context3.property("cx3_func", cx2_func);
        JSValue forty_two = context3.evaluateScript("cx3_func()");
        assertTrue(forty_two.toNumber().equals(42.0));

        JSContextInGroup context4 = new JSContextInGroup(contextGroup);
        context4.property("testObject", cx2_func);
        ret = context4.evaluateScript("func1()");
        assertTrue(ret.toNumber().equals(42.0));

        assertEquals(context2.getGroup(),context3.getGroup());
        assertEquals(context3.getGroup(),context4.getGroup());
        assertNotEquals(context4.getGroup(),null);
        assertNotEquals(context1.getGroup(),context2.getGroup());

        context.garbageCollect();
        context1.garbageCollect();
        context2.garbageCollect();
        context3.garbageCollect();
        context4.garbageCollect();
    }

    private boolean excp;

    @org.junit.Test
    public void testJSContextExceptionHandler() throws Exception {
        JSContext context = new JSContext();
        try {
            context.property("does_not_exist").toFunction();
            assertTrue(false);
        } catch (JSException e) {
            assertTrue(true);
        }

        excp = false;
        context.setExceptionHandler(new JSContext.IJSExceptionHandler() {
            @Override
            public void handle(JSException e) {
                excp = !excp;
            }
        });
        try {
            context.property("does_not_exist").toFunction();
            assertTrue(excp);
        } catch (JSException e) {
            assertTrue(false);
        }

        context.clearExceptionHandler();
        try {
            context.property("does_not_exist").toFunction();
            assertTrue(false);
        } catch (JSException e) {
            // excp should still be true
            assertTrue(excp);
        }

        excp = false;
        final JSContext context2 = new JSContext();
        context2.setExceptionHandler(new JSContext.IJSExceptionHandler() {
            @Override
            public void handle(JSException e) {
                excp = !excp;
                // Raise another exception.  Should throw JSException
                context2.property("does_not_exist").toFunction();
            }
        });
        try {
            context2.property("does_not_exist").toFunction();
            assertTrue(false);
        } catch (JSException e) {
            assertTrue(excp);
        }

        context.garbageCollect();
        context2.garbageCollect();
    }

    @org.junit.Test
    public void testJSContextEvaluateScript() throws Exception {
        final String script1 = "" +
                "var val1 = 1;\n" +
                "var val2 = 'foo';\n" +
                "does_not_exist(do_something);";
        String url = "http://liquidplayer.com/script1.js";

        JSContext context = new JSContext();
        try {
            context.evaluateScript(script1, null, url, 1);
            assertTrue(false);
        } catch (JSException e) {
            String stack = e.getError().toObject().property("stack").toString();
            String expected = "global code@" + url + ":3:";
            assertEquals(stack.substring(0,expected.length()),expected);
        }

        context.property("localv",69);
        JSValue val = context.evaluateScript("this.localv",null);
        assertTrue(val.toNumber().equals(69.0));
        JSObject obj = new JSObject(context);
        obj.property("localv",100);
        val = context.evaluateScript("this.localv",obj);
        assertTrue(val.toNumber().equals(100.0));

        val = context.evaluateScript("this.localv");
        assertTrue(val.toNumber().equals(69.0));

        context.garbageCollect();
    }
}