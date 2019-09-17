package com.github.arielcarrera.cdi.test.config;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.cache.spi.entry.CacheEntry;
import org.infinispan.commands.read.GetAllCommand;
import org.infinispan.commands.read.GetCacheEntryCommand;
import org.infinispan.commands.read.GetKeyValueCommand;
import org.infinispan.commands.write.PutKeyValueCommand;
import org.infinispan.context.InvocationContext;
import org.infinispan.interceptors.BaseCustomAsyncInterceptor;

@Slf4j
public class HibernateCacheInterceptor extends BaseCustomAsyncInterceptor {

    private static List<String> history = new ArrayList<>();
    
    
    public static void clearHistory() {
	history = new ArrayList<>();
    }
    
    public static List<String> getHistory() {
	return history;
    }
    
    @Override
    public Object visitPutKeyValueCommand(InvocationContext ctx, PutKeyValueCommand command) throws Throwable {
	log.info("Ejecutando Interceptor PUT de Cache Infinispan - key: " + command.getKey() + " - clase: "
		+ ((CacheEntry) command.getValue()).getSubclass() + " - valor serializado: "
		+ ((CacheEntry) command.getValue()).getDisassembledState());
	Object invokeNext = super.invokeNext(ctx, command);
	history.add("PUT:" + command.getKey().toString() + "=" + (invokeNext == null ? "FALSE" : "TRUE"));
	return invokeNext;
    }

    @Override
    public Object visitGetCacheEntryCommand(InvocationContext ctx, GetCacheEntryCommand command) throws Throwable {
	log.info("Ejecutando Interceptor GET ENTRY de Cache Infinispan - key: " + command.getKey() + " - segmento: "
		+ command.getSegment());
	Object obj = handleDefault(ctx, command);
	history.add("GETENTRY:" + command.getKey().toString() + "=" + (obj == null ? "FALSE" : "TRUE"));
	return obj;
    }

    @Override
    public Object visitGetKeyValueCommand(InvocationContext ctx, GetKeyValueCommand command) throws Throwable {
	log.info("Ejecutando Interceptor GET KEY VALUE de Cache Infinispan - key: " + command.getKey() + " - segmento: "
		+ command.getSegment());
	Object visitGetKeyValueCommand = super.visitGetKeyValueCommand(ctx, command);
	history.add("GET:" + command.getKey().toString() + "=" + (visitGetKeyValueCommand == null ? "FALSE" : "TRUE"));
	return visitGetKeyValueCommand;
    }

    @Override
    public Object visitGetAllCommand(InvocationContext ctx, GetAllCommand command) throws Throwable {
	log.info("Ejecutando Interceptor GET KEY VALUE de Cache Infinispan - key: " + command.getKeys().toString());
	Object visitGetAllCommand = super.visitGetAllCommand(ctx, command);
	history.add("GETALL:" + command.getKeys().toString() + "=" + (visitGetAllCommand == null ? "FALSE" : "TRUE"));
	return visitGetAllCommand;
    }

}