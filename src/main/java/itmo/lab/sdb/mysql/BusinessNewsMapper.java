package itmo.lab.sdb.mysql;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BusinessNewsMapper implements RowMapper<BusinessNews> {
    @Override
    public BusinessNews mapRow(ResultSet rs, int rowNum) throws SQLException {
        BusinessNews businessNews = new BusinessNews();
        businessNews.setTitle(rs.getString("TITLE"));
        businessNews.setSummary(rs.getString("SUMMARY"));
        businessNews.setPublished(rs.getString("PUBLISHED"));
        return businessNews;
    }
}
