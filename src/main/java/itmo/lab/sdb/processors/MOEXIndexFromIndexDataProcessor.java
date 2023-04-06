package itmo.lab.sdb.processors;

import itmo.lab.sdb.entities.IndexData;
import itmo.lab.sdb.entities.MOEXIndexResult;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;

public class MOEXIndexFromIndexDataProcessor implements ItemProcessor<IndexData, MOEXIndexResult> {
    @Override
    public MOEXIndexResult process(IndexData item) throws Exception {
        MOEXIndexResult indexResult = new MOEXIndexResult();
        indexResult.setIndexValue(Double.parseDouble(item.getClose().replace(",", ".")));
        indexResult.setDate(LocalDate.parse(item.getTradeDate()));

        return indexResult;
    }
}
