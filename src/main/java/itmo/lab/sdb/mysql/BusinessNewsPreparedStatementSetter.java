package itmo.lab.sdb.mysql;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BusinessNewsPreparedStatementSetter implements ItemPreparedStatementSetter<BusinessNews> {
    @Override
    public void setValues(BusinessNews item, PreparedStatement ps) throws SQLException {
        ps.setString(1, item.getTitle());
        ps.setString(2, item.getScore());
        ps.setString(3, item.getLink());
        ps.setString(4, item.getSummary());
        ps.setString(5, item.getPublished());
        ps.setString(6, String.join(",", item.getTickers()));
    }
}
