 package app_.smartreceipt.digitaldocs.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.model.Currency;
import app_.smartreceipt.digitaldocs.model.ReceiptEntity;
import app_.smartreceipt.digitaldocs.model.ReceiptList;

public class ChartActivity extends AppCompatActivity {

    enum Selection {
        WEEK,
        MONTH,
        YEAR
    }

    private Button weekButton;
    private Button monthButton;
    private Button yearButton;

    private TextView dateText;
    private ImageButton next;
    private ImageButton previous;
    private TextView percentNumber;
    private ImageView percentImage;
    private TextView weekDates;

    private ImageButton camera;
    private ImageButton chart;
    private ImageButton settings;
    private ImageButton receipt;
    private ImageButton search;

    private Selection selection = Selection.MONTH;
    private Calendar currentDate = Calendar.getInstance();
    private ArrayList<ReceiptEntity> receipts = ReceiptList.getReceipts();

    private BarChart barChart;

    private final String[] monthNames = new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private final String[] weekDayName = new String[] {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_activity);
        setup();
        setUpBarChat();
        setDataToMonth();
    }

    private void setUpBarChat() {
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getLegend().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBorders(false);
        barChart.setMaxVisibleValueCount(0);
        barChart.getDescription().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);


        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);

        ValueFormatter yFormatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return Currency.getSymbol() + value;
            }
        };

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextSize(15);
        leftAxis.setGranularity(0.01f);
        leftAxis.setValueFormatter(yFormatter);

        barChart.setNoDataText("No receipts for this time period!");
        barChart.animateY(500);
        setUpListener();
    }

    private void setUpListener() {
        final Intent intent = new Intent(this, ReceiptGraphActivity.class);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e.getY() <= 0) {
                    return;
                }
                int dateSelected = (int) e.getX();

                ArrayList<ReceiptEntity> receiptOnSelected = new ArrayList<ReceiptEntity>();
                String mainText = "";

                int year = currentDate.get(Calendar.YEAR);
                int month = currentDate.get(Calendar.MONTH) + 1;
                for (ReceiptEntity receipt : receipts) {
                    String[] date = receipt.getDate().split("/");
                    int repYear = Integer.parseInt(date[0]);
                    int repMonth = Integer.parseInt(date[1]);
                    int repDay = Integer.parseInt(date[2]);

                    if (selection == Selection.MONTH) {
                        if (repYear == year && repMonth == month && repDay == dateSelected) {
                            receiptOnSelected.add(receipt);

                            Calendar cal = Calendar.getInstance();
                            cal.set(repYear,repMonth - 1, dateSelected);

                            mainText = df.format(cal.getTime());

                        }
                    } else if (selection == Selection.YEAR) {
                        if (repYear == year && repMonth == dateSelected) {
                            receiptOnSelected.add(receipt);
                            currentDate.set(Calendar.MONTH, dateSelected - 1);

                            String[] yearString = df.format(currentDate.getTime()).split(" ");
                            int index = 0;

                            for (int i = 0; i < yearString.length; i++) {
                                try {
                                    Double.parseDouble(yearString[i]);
                                } catch (Exception error) {
                                    char ca = yearString[i].toCharArray()[0];
                                    if (Character.isLetter(ca)) {
                                        index = i;
                                    }
                                }
                            }

                            mainText =  yearString[index] + " " + year;
                        }
                    } else if (selection == Selection.WEEK) {
                        Calendar comparisonCal = Calendar.getInstance();
                        comparisonCal.setFirstDayOfWeek(Calendar.MONDAY);
                        comparisonCal.set(repYear,repMonth-1,repDay);
                        if (year == repYear && currentDate.get(Calendar.WEEK_OF_YEAR) == comparisonCal.get(Calendar.WEEK_OF_YEAR)) {
                            int dayOfWeek = comparisonCal.get(Calendar.DAY_OF_WEEK) - 1;
                            if (dayOfWeek == 0) {
                                dayOfWeek = 7;
                            }
                            if (dayOfWeek == dateSelected) {
                                receiptOnSelected.add(receipt);
                                mainText =  currentDate.get(Calendar.WEEK_OF_YEAR) + " Week " + year;
                            }
                        }
                    }

                }


                Bundle bundle = new Bundle();
                bundle.putSerializable("receipt", receiptOnSelected);
                bundle.putString("text", mainText);
                intent.putExtras(bundle);
                ChartActivity.this.startActivity(intent);
            }

            @Override
            public void onNothingSelected() {
                //do nothing
            }
        });
    }

    private void setDataToMonth() {
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH) + 1;

        int maxDaysinMonth = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH);

        double[] totalPerDay = new double[maxDaysinMonth];

        double totalThisMonth = 0;
        double totalLastMonth = 0;

        for (ReceiptEntity receipt : receipts) {
            String[] date = receipt.getDate().split("/");
            int repYear = Integer.parseInt(date[0]);
            int repMonth = Integer.parseInt(date[1]);
            int repDay = Integer.parseInt(date[2]);

            if (year == repYear && month == repMonth) {
                totalPerDay[repDay - 1] = totalPerDay[repDay - 1] + receipt.getTotalPrice();
                totalThisMonth += receipt.getTotalPrice();
            } else if (year == repYear && month - 1 == repMonth) {
                totalLastMonth += receipt.getTotalPrice();
            }
        }

        display(totalPerDay);
        displayPercentage(totalThisMonth, totalLastMonth);
    }

    private void displayPercentage(double totalThis, double totalLast) {

        if (totalThis == 0) {
            totalThis = 0.01;
        }
        if (totalLast == 0) {
            totalLast = 0.01;
        }

        double percentageDifference = (totalThis - totalLast)/totalLast * 100;

        if (percentageDifference > 1000) {
            percentageDifference = 999;
        }

        percentageDifference = Math.round(percentageDifference);
        percentNumber.setText(((int)percentageDifference) + "%");

        if (percentageDifference > 0) {
            percentNumber.setTextColor(getResources().getColor(R.color.up_trend));
            percentImage.setImageResource(R.drawable.baseline_trending_up_24);
        } else {
            percentNumber.setTextColor(getResources().getColor(R.color.down_trend));
            percentImage.setImageResource(R.drawable.baseline_trending_down_24);
        }
    }

    private void display(double[] total) {

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < total.length; i++) {
            entries.add(new BarEntry(i + 1,(float) total[i]));
        }

        ValueFormatter xFormatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                if (selection == Selection.MONTH) {
                    return "" + (int) value;
                } else if (selection == Selection.YEAR) {
                    return monthNames[((int) value) - 1];
                } else {
                    return weekDayName[((int) value) -1];
                }
            }
        };
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(xFormatter);

        BarDataSet bardataset = new BarDataSet(entries, "");

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(bardataset);

        BarData data = new BarData(dataSets);
        //bardataset.setColor(ColorTemplate.rgb("#00008b"));
        List<GradientColor> gradientColors = new ArrayList<GradientColor>() {};
        gradientColors.add(new GradientColor(ColorTemplate.rgb("#cc2b5e"), ColorTemplate.rgb("#753a88")));
        bardataset.setGradientColors(gradientColors);
        barChart.setData(data); // set the data and list of labels into chart
        barChart.invalidate();
        barChart.animateY(500);
    }

    private void setDataToYear() {
        int year = currentDate.get(Calendar.YEAR);

        double[] totalPerYear = new double[12];

        double totalThisYear = 0;
        double totalLastYear = 0;

        for (ReceiptEntity receipt : receipts) {
            String[] date = receipt.getDate().split("/");
            int repYear = Integer.parseInt(date[0]);
            int repMonth = Integer.parseInt(date[1]);

            if (year == repYear) {
                totalPerYear[repMonth - 1] = totalPerYear[repMonth - 1] + receipt.getTotalPrice();
                totalThisYear += receipt.getTotalPrice();
            } else if (year - 1 == repYear) {
                totalLastYear += receipt.getTotalPrice();
            }
        }
        display(totalPerYear);
        displayPercentage(totalThisYear, totalLastYear);
    }

    private void setDataWeek() {
        int year = currentDate.get(Calendar.YEAR);
        Calendar comparisonCal = Calendar.getInstance();
        comparisonCal.setFirstDayOfWeek(Calendar.MONDAY);

        double[] totalPerWeek = new double[7];
        double totalThisWeek = 0;
        double totalLastWeek = 0;

        for (ReceiptEntity receipt : receipts) {
            String[] date = receipt.getDate().split("/");
            int repYear = Integer.parseInt(date[0]);
            int repMonth = Integer.parseInt(date[1]);
            int repDay = Integer.parseInt(date[2]);
            comparisonCal.set(repYear,repMonth - 1, repDay);

            if (year == repYear && currentDate.get(Calendar.WEEK_OF_YEAR) == comparisonCal.get(Calendar.WEEK_OF_YEAR)) {
                int dayOfWeek = comparisonCal.get(Calendar.DAY_OF_WEEK) - 1;
                if (dayOfWeek == 0) {
                    dayOfWeek = 7;
                }
                totalPerWeek[dayOfWeek - 1] = totalPerWeek[dayOfWeek - 1] + receipt.getTotalPrice();
                totalThisWeek += receipt.getTotalPrice();
            }
            comparisonCal.add(Calendar.WEEK_OF_YEAR, 1);
            if (year == repYear && currentDate.get(Calendar.WEEK_OF_YEAR) == comparisonCal.get(Calendar.WEEK_OF_YEAR)) {
                totalLastWeek += receipt.getTotalPrice();
            }
        }

        display(totalPerWeek);
        displayPercentage(totalThisWeek, totalLastWeek);
    }
    private void setup() {
        currentDate.setFirstDayOfWeek(Calendar.MONDAY);
        setScene();
        linkCamera();
        linkChart();
        linkReceipt();
        linkSetting();
        linkSearch();
        linkPrevious();
        linkNext();
        linkYear();
        linkMonth();
        linkWeek();
    }

    private void setScene() {
        barChart = findViewById(R.id.barchart);

        weekButton = findViewById(R.id.button_week);
        monthButton = findViewById(R.id.button_month);
        yearButton = findViewById(R.id.button_year_chart);


        next = findViewById(R.id.date_plus);
        previous = findViewById(R.id.date_minus);
        dateText = findViewById(R.id.month_year_text);

        //Default toolbar setup
        camera = findViewById(R.id.camera_widget_chat);
        chart = findViewById(R.id.receipt_chart_chart);
        settings = findViewById(R.id.setting_widget_chat);
        receipt = findViewById(R.id.receipt_widget_chat);
        search = findViewById(R.id.search_widget_chat);

        percentNumber = findViewById(R.id.percent);
        percentImage = findViewById(R.id.percent_sign);
        weekDates = findViewById(R.id.week_date);
        weekDates.bringToFront();
    }

    private void linkYear() {


        yearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yearButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#174891")));
                monthButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AA174891")));
                weekButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AA174891")));
                dateText.setText(("" + currentDate.get(Calendar.YEAR)));
                selection = Selection.YEAR;
                setWeekDates();
                setDataToYear();
            }
        });
    }

    private void linkMonth() {


        monthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yearButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AA174891")));
                monthButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#174891")));
                weekButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AA174891")));
                dateText.setText((currentDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + currentDate.get(Calendar.YEAR)));
                selection = Selection.MONTH;
                setWeekDates();
                setDataToMonth();

            }
        });
    }

    private void linkWeek() {
        weekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yearButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AA174891")));
                monthButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AA174891")));
                weekButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#174891")));
                dateText.setText(("Week " + currentDate.get(Calendar.WEEK_OF_YEAR) + " " + currentDate.get(Calendar.YEAR)));
                selection = Selection.WEEK;
                setWeekDates();
                setDataWeek();
            }
        });
    }

    private void linkPrevious() {
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selection == Selection.MONTH) {
                    currentDate.add(Calendar.MONTH, -1);
                    dateText.setText((currentDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + currentDate.get(Calendar.YEAR)));
                    setDataToMonth();
                } else if (selection == Selection.YEAR) {
                    currentDate.add(Calendar.YEAR, -1);
                    dateText.setText((" " + currentDate.get(Calendar.YEAR)));
                    setDataToYear();
                } else {
                    currentDate.add(Calendar.WEEK_OF_YEAR, -1);
                    dateText.setText(("Week " + currentDate.get(Calendar.WEEK_OF_YEAR) + " " + currentDate.get(Calendar.YEAR)));
                    setDataWeek();
                }
                setWeekDates();
            }
        });
    }

    private void linkNext() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selection == Selection.MONTH) {
                    currentDate.add(Calendar.MONTH, 1);
                    dateText.setText((currentDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + currentDate.get(Calendar.YEAR)));
                    setDataToMonth();
                } else if (selection == Selection.YEAR) {
                    currentDate.add(Calendar.YEAR, 1);
                    dateText.setText((" " + currentDate.get(Calendar.YEAR)));
                    setDataToYear();
                } else {
                    currentDate.add(Calendar.WEEK_OF_YEAR, 1);
                    dateText.setText(("Week " + currentDate.get(Calendar.WEEK_OF_YEAR) + " " + currentDate.get(Calendar.YEAR)));
                    setDataWeek();
                }
                setWeekDates();
            }
        });
    }

    private void setWeekDates() {
        if (selection != Selection.WEEK) {
            weekDates.setText("");
            return;
        }
        String dates = "";
        Calendar setCal = (Calendar) currentDate.clone();
        setCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        //DateFormat weekDf = DateFormat.getDateInstance("", Locale.getDefault());
        DateFormat formatter = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
        dates += formatter.format(setCal.getTime()) + " - ";
        setCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        dates += formatter.format(setCal.getTime());
        weekDates.setText(dates);
    }

    private void linkCamera() {
        final Intent intent = new Intent(this, CameraActivity.class);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChartActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * Accessing the search page from the receipt page
     */

    private void linkSearch() {
        final Intent intent = new Intent(this, SearchActivity.class);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChartActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * Accessing the profile page from the receipt page
     */

    private void linkChart() {
        final Intent intent = new Intent(this, ChartActivity.class);

        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChartActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * Accessing the setting page from the receipt receipt page
     */

    private void linkSetting() {
        final Intent intent = new Intent(this, SettingsActivity.class);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChartActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * Accessing the receipt page from the receipt page
     */

    private void linkReceipt() {
        final Intent intent = new Intent(this, ReceiptActivity.class);

        receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChartActivity.this.startActivity(intent);
            }
        });
    }
}
