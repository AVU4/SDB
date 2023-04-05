package itmo.lab.sdb.mappers;

import itmo.lab.sdb.entities.MOEXIndexResult;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MOEXIndexResultMapper implements RowMapper<MOEXIndexResult> {
    @Override
    public MOEXIndexResult mapRow(ResultSet rs, int rowNum) throws SQLException {
        MOEXIndexResult moexIndexResult = new MOEXIndexResult();
        moexIndexResult.setDayId(rs.getLong("date"));
        moexIndexResult.setTitle(rs.getString("title"));
        String summary = rs.getString("summary").replace("\n", " ");
        moexIndexResult.setSummary(summary);
        moexIndexResult.setIndexValue(rs.getDouble("index_value"));
        return moexIndexResult;
    }
}
