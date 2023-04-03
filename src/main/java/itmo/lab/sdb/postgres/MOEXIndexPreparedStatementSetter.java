package itmo.lab.sdb.postgres;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MOEXIndexPreparedStatementSetter implements ItemPreparedStatementSetter<MOEXIndexResult> {
    @Override
    public void setValues(MOEXIndexResult item, PreparedStatement ps) throws SQLException {
        ps.setLong(1, item.getDayId());
        ps.setString(2, item.getTitle());
        ps.setString(3, item.getSummary());
        ps.setArray(4, null);
    }
}
