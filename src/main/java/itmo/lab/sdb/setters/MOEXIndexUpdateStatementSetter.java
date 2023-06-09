package itmo.lab.sdb.setters;

import itmo.lab.sdb.entities.MOEXIndexResult;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MOEXIndexUpdateStatementSetter implements ItemPreparedStatementSetter<MOEXIndexResult> {
    @Override
    public void setValues(MOEXIndexResult item, PreparedStatement ps) throws SQLException {
        ps.setDouble(1, item.getIndexValue());
        ps.setDate(2, Date.valueOf(item.getDate()));
    }
}
