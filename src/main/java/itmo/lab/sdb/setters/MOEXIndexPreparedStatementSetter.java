package itmo.lab.sdb.setters;

import itmo.lab.sdb.entities.MOEXIndexResult;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MOEXIndexPreparedStatementSetter implements ItemPreparedStatementSetter<MOEXIndexResult> {
    @Override
    public void setValues(MOEXIndexResult item, PreparedStatement ps) throws SQLException {
        ps.setDate(1, Date.valueOf(item.getDate()));
        ps.setString(2, item.getTitle());
        ps.setString(3, item.getSummary());
        ps.setDouble(4, 0);
    }
}
