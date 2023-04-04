package itmo.lab.sdb.processors;

import itmo.lab.sdb.entities.IndexData;
import itmo.lab.sdb.entities.MOEXIndexResult;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MOEXIndexFromIndexDataProcessor implements ItemProcessor<IndexData, MOEXIndexResult> {
    @Override
    public MOEXIndexResult process(IndexData item) throws Exception {
        MOEXIndexResult indexResult = new MOEXIndexResult();
        indexResult.setIndexValue(Double.parseDouble(item.getClose().replace(",", ".")));
        LocalDate date = LocalDate.parse(item.getTradeDate());
        indexResult.setDayId(date.toEpochDay());

        return indexResult;
    }
}
