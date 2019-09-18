package com.github.arielcarrera.cdi.test.config;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.cache.spi.entry.CacheEntry;
import org.infinispan.commands.VisitableCommand;
import org.infinispan.commands.functional.ReadOnlyKeyCommand;
import org.infinispan.commands.functional.ReadOnlyManyCommand;
import org.infinispan.commands.functional.ReadWriteKeyCommand;
import org.infinispan.commands.functional.ReadWriteKeyValueCommand;
import org.infinispan.commands.functional.ReadWriteManyCommand;
import org.infinispan.commands.functional.ReadWriteManyEntriesCommand;
import org.infinispan.commands.functional.WriteOnlyKeyCommand;
import org.infinispan.commands.functional.WriteOnlyKeyValueCommand;
import org.infinispan.commands.functional.WriteOnlyManyCommand;
import org.infinispan.commands.functional.WriteOnlyManyEntriesCommand;
import org.infinispan.commands.read.GetAllCommand;
import org.infinispan.commands.read.GetCacheEntryCommand;
import org.infinispan.commands.read.GetKeyValueCommand;
import org.infinispan.commands.write.PutKeyValueCommand;
import org.infinispan.commands.write.RemoveCommand;
import org.infinispan.commands.write.ReplaceCommand;
import org.infinispan.context.InvocationContext;
import org.infinispan.interceptors.BaseCustomAsyncInterceptor;

@Slf4j
@NoArgsConstructor
public class TestInfinispanCacheInterceptor extends BaseCustomAsyncInterceptor {

    private static List<String> history = new ArrayList<>();

    public static void clearHistory() {
	history = new ArrayList<>();
    }

    public static List<String> getHistory() {
	return history;
    }

    @Override
    protected Object handleDefault(InvocationContext ctx, VisitableCommand command) throws Throwable {
	log.debug("Calling HandleDefault (interceptor)");
	return super.invokeNext(ctx, command);
    }

    @Override
    public Object visitPutKeyValueCommand(InvocationContext ctx, PutKeyValueCommand command) throws Throwable {
	log.info("Procesing Interceptor PUT (Cache Infinispan) - key: " + command.getKey() + " - clase: "
		+ ((CacheEntry) command.getValue()).getSubclass() + " - valor serializado: "
		+ ((CacheEntry) command.getValue()).getDisassembledState());
	Object invokeNext = super.invokeNext(ctx, command);
	history.add("PUT:" + command.getKey().toString() + "=" + (invokeNext == null ? "FALSE" : "TRUE"));
	return invokeNext;
    }

    @Override
    public Object visitGetCacheEntryCommand(InvocationContext ctx, GetCacheEntryCommand command) throws Throwable {
	log.info("Procesing Interceptor GET ENTRY (Cache Infinispan) - key: " + command.getKey() + " - segmento: "
		+ command.getSegment());
	Object obj = handleDefault(ctx, command);
	history.add("GETENTRY:" + command.getKey().toString() + "=" + (obj == null ? "FALSE" : "TRUE"));
	return obj;
    }

    @Override
    public Object visitGetKeyValueCommand(InvocationContext ctx, GetKeyValueCommand command) throws Throwable {
	log.info("Procesing Interceptor GET KEY VALUE (Cache Infinispan) - key: " + command.getKey() + " - segmento: "
		+ command.getSegment());
	Object visitGetKeyValueCommand = super.visitGetKeyValueCommand(ctx, command);
	history.add("GET:" + command.getKey().toString() + "=" + (visitGetKeyValueCommand == null ? "FALSE" : "TRUE"));
	return visitGetKeyValueCommand;
    }

    @Override
    public Object visitGetAllCommand(InvocationContext ctx, GetAllCommand command) throws Throwable {
	log.info("Procesing Interceptor GET ALL (Cache Infinispan) - key: " + command.getKeys().toString());
	Object visitGetAllCommand = super.visitGetAllCommand(ctx, command);
	history.add("GETALL:" + command.getKeys().toString() + "=" + (visitGetAllCommand == null ? "FALSE" : "TRUE"));
	return visitGetAllCommand;
    }

    @Override
    public Object visitRemoveCommand(InvocationContext ctx, RemoveCommand command) throws Throwable {
	log.info("Procesing Interceptor VISIT REMOVE (Cache Infinispan) - key: " + command.getKey().toString());
	Object visit = super.visitRemoveCommand(ctx, command);
	history.add("REMOVE:" + command.getKey().toString() + "=" + (visit == null ? "FALSE" : "TRUE"));
	return visit;
    }

    @Override
    public Object visitReadOnlyKeyCommand(InvocationContext ctx, ReadOnlyKeyCommand command) throws Throwable {
	log.info(
		"Procesing Interceptor VISIT READ ONLY KEY (Cache Infinispan) - key: " + command.getKey().toString());
	Object visit = super.visitReadOnlyKeyCommand(ctx, command);
	history.add("READONLYKEY:" + command.getKey().toString() + "=" + (visit == null ? "FALSE" : "TRUE"));
	return visit;
    }

    @Override
    public Object visitReadOnlyManyCommand(InvocationContext ctx, ReadOnlyManyCommand command) throws Throwable {
	log.info("Procesing Interceptor VISIT READ ONLY MANY (Cache Infinispan) - key: "
		+ command.getKeys().toString());
	Object visit = super.visitReadOnlyManyCommand(ctx, command);
	history.add("READONLYMANY:" + command.getKeys().toString() + "=" + (visit == null ? "FALSE" : "TRUE"));
	return visit;
    }

    @Override
    public Object visitWriteOnlyKeyCommand(InvocationContext ctx, WriteOnlyKeyCommand command) throws Throwable {
	log.info("Procesing Interceptor VISIT WRITE KEY (Cache Infinispan) - key: " + command.getKey().toString());
	Object visit = super.visitWriteOnlyKeyCommand(ctx, command);
	history.add("WRITEKEY:" + command.getKey().toString() + "=" + (visit == null ? "FALSE" : "TRUE"));
	return visit;
    }

    @Override
    public Object visitReadWriteKeyValueCommand(InvocationContext ctx, ReadWriteKeyValueCommand command)
	    throws Throwable {
	log.info("Procesing Interceptor VISIT READ WRITE KEY VALUE (Cache Infinispan) - key: "
		+ command.getKey().toString());
	Object visit = super.visitReadWriteKeyValueCommand(ctx, command);
	history.add("READWRITEKEYVALUE:" + command.getKey().toString() + "=" + (visit == null ? "FALSE" : "TRUE"));
	return visit;
    }

    @Override
    public Object visitReadWriteKeyCommand(InvocationContext ctx, ReadWriteKeyCommand command) throws Throwable {
	log.info("Procesing Interceptor VISIT READ WRITE KEY (Cache Infinispan) - key: "
		+ command.getKey().toString());
	Object visit = super.visitReadWriteKeyCommand(ctx, command);
	history.add("READWRITEKEY:" + command.getKey().toString() + "=" + (visit == null ? "FALSE" : "TRUE"));
	return visit;
    }

    @Override
    public Object visitReadWriteManyCommand(InvocationContext ctx, ReadWriteManyCommand command) throws Throwable {
	log.info("Procesing Interceptor VISIT READ WRITE MANY (Cache Infinispan) - key: "
		+ command.getAffectedKeys().toString());
	Object visit = super.visitReadWriteManyCommand(ctx, command);
	history.add("READWRITEMANY:" + command.getAffectedKeys().toString() + "=" + (visit == null ? "FALSE" : "TRUE"));
	return visit;
    }

    @Override
    public Object visitReadWriteManyEntriesCommand(InvocationContext ctx, ReadWriteManyEntriesCommand command)
	    throws Throwable {
	log.info("Procesing Interceptor VISIT READ WRITE MANY ENTRIES (Cache Infinispan) - key: "
		+ command.getAffectedKeys().toString());
	Object visit = super.visitReadWriteManyEntriesCommand(ctx, command);
	history.add("READWRITEMANYENTRIES:" + command.getAffectedKeys().toString() + "="
		+ (visit == null ? "FALSE" : "TRUE"));
	return visit;
    }

    @Override
    public Object visitWriteOnlyKeyValueCommand(InvocationContext ctx, WriteOnlyKeyValueCommand command)
	    throws Throwable {
	log.info("Procesing Interceptor VISIT WRITE KEY VALUE (Cache Infinispan) - key: "
		+ command.getKey().toString());
	Object visit = super.visitWriteOnlyKeyValueCommand(ctx, command);
	history.add("WRITEKEYVALUE:" + command.getKey().toString() + "=" + (visit == null ? "FALSE" : "TRUE"));
	return visit;
    }

    @Override
    public Object visitWriteOnlyManyCommand(InvocationContext ctx, WriteOnlyManyCommand command) throws Throwable {
	log.info("Procesing Interceptor VISIT WRITE KEY VALUE (Cache Infinispan) - key: "
		+ command.getAffectedKeys().toString());
	Object visit = super.visitWriteOnlyManyCommand(ctx, command);
	history.add("WRITEMANY:" + command.getAffectedKeys().toString() + "=" + (visit == null ? "FALSE" : "TRUE"));
	return visit;
    }

    @Override
    public Object visitWriteOnlyManyEntriesCommand(InvocationContext ctx, WriteOnlyManyEntriesCommand command)
	    throws Throwable {
	log.info("Procesing Interceptor VISIT WRITE MANY ENTRIES (Cache Infinispan) - key: "
		+ command.getAffectedKeys().toString());
	Object visit = super.visitWriteOnlyManyEntriesCommand(ctx, command);
	history.add(
		"WRITEMANYENTRIES:" + command.getAffectedKeys().toString() + "=" + (visit == null ? "FALSE" : "TRUE"));
	return visit;
    }

    @Override
    public Object visitReplaceCommand(InvocationContext ctx, ReplaceCommand command) throws Throwable {
	log.info("Procesing Interceptor VISIT REPLACE (Cache Infinispan) - key: " + command.getKey().toString());
	Object visit = super.visitReplaceCommand(ctx, command);
	history.add("REPLACE:" + command.getKey().toString() + "=" + (visit == null ? "FALSE" : "TRUE"));
	return visit;
    }

}