package itmo.lab.sdb.processors;

import itmo.lab.sdb.entities.BusinessNews;
import org.springframework.batch.item.ItemProcessor;

public class ConsoleOutputProcessor implements ItemProcessor<BusinessNews, BusinessNews> {
    @Override
    public BusinessNews process(BusinessNews item) throws Exception {
        System.out.println("Title - " + item.getTitle());
        return item;
    }
}
