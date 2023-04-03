package itmo.lab.sdb.processors;

import itmo.lab.sdb.mysql.BusinessNews;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ConsoleOutputProcessor implements ItemProcessor<BusinessNews, BusinessNews> {
    @Override
    public BusinessNews process(BusinessNews item) throws Exception {
        System.out.println("Title - " + item.getTitle());
        return item;
    }
}
