package itmo.lab.sdb.events;

import org.springframework.context.ApplicationEvent;

public class DatabaseDataImportedEvent extends ApplicationEvent {

    public DatabaseDataImportedEvent(Object source) {
        super(source);
    }
}
