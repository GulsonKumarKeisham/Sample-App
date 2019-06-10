package com.test.testapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class GraphActivity extends AppCompatActivity {
    private ArrayList<String[]> employeeData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Intent intent = getIntent();
        employeeData = (ArrayList<String[]>) intent.getSerializableExtra("employee_list");

        AnyChartView anyChartView = findViewById(R.id.chart);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        for(int i=0; i<10; i++) {
            data.add(new ValueDataEntry(employeeData.get(i)[0], extractSalaryInNumber(employeeData.get(i)[5])));
        }

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("${%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title("First 10 Employee Salary Bar Chart");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Employee");
        cartesian.yAxis(0).title("Salary");

        anyChartView.setChart(cartesian);
    }

    /**
     * method to substring salary by removing $
     * return only number value
     * @param salary
     * @return
     */

    private int extractSalaryInNumber(String salary){
        return Integer.parseInt(salary.substring(1).replace(",",""));
    }
}