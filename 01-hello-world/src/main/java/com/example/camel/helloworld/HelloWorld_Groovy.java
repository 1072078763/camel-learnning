package com.example.camel.helloworld;

import org.apache.camel.impl.DefaultCamelContext;
import org.codehaus.groovy.jsr223.GroovyScriptEngineFactory;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import java.io.InputStream;

public class HelloWorld_Groovy {
    public static void main(String[] args) throws Exception{
        DefaultCamelContext camelContext=new DefaultCamelContext();
        camelContext.start();
        //获取groovy脚本字符串
        InputStream groovyStream=HelloWorld_Groovy.class.getResourceAsStream("/route.groovy");
        byte[] data=new byte[groovyStream.available()];
        groovyStream.read(data);
        groovyStream.close();
        String groovy=new String(data);
        //执行脚本
        GroovyScriptEngineFactory groovyScriptEngineFactory=new GroovyScriptEngineFactory();
        ScriptEngine engine = groovyScriptEngineFactory.getScriptEngine();
        Bindings binding=engine.createBindings();
        binding.put("context",camelContext);
        engine.eval(groovy,binding);
    }
}
