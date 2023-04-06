package itmo.lab.sdb.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MOEXIndexResult {
    private LocalDate date;
    private String title;
    private String summary;
    private double indexValue;
}
