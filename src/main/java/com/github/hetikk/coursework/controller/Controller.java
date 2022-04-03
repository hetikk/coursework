package com.github.hetikk.coursework.controller;

import com.github.hetikk.coursework.CalculationInput;
import com.github.hetikk.coursework.CalculationOutput;
import com.github.hetikk.coursework.math.Implementations;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class Controller {

    @FXML
    private TextField function;

    @FXML
    private TextField integrationLowerLimit;

    @FXML
    private TextField integrationUpperLimit;

    @FXML
    private TextField threadCount;

    @FXML
    private ChoiceBox<String> methodChoicer;

    @FXML
    private Button calculate;

    @FXML
    private Label result;

    @FXML
    private Label timer;

    @FXML
    private StackedAreaChart<Number, Number> chart;

    private final Map<String, Function<CalculationInput, CalculationOutput>> availableMethods;

    public Controller() {
        availableMethods = new LinkedHashMap<String, Function<CalculationInput, CalculationOutput>>() {{
            put("Метод трапеций", Implementations::method1);
            put("Метод трапеций (многопоточный)", Implementations::method1_multithreaded);
//            put("Метод левых прямоугольников", Implementations::method2);
//            put("Метод правых прямоугольников", Implementations::method3);
//            put("Метод средних прямоугольников", Implementations::method4);
//            put("Метод Симсона", Implementations::method5);
        }};
    }

    @FXML
    void initialize() {
        chart.setHorizontalGridLinesVisible(true);
        methodChoicer.setItems(FXCollections.observableList(new ArrayList<>(availableMethods.keySet())));
        String defaultMethod = availableMethods.keySet().stream().findFirst().get();
        methodChoicer.setValue(defaultMethod);
        threadCount.setText(Runtime.getRuntime().availableProcessors() + "");

        calculate.setOnAction(event -> {
            boolean funcIsValid = !function.getText().trim().isEmpty();
            boolean aIsValid = integrationLowerLimit.getText().trim().matches("-?[0-9]+([.,][0-9]+)?");
            boolean bIsValid = integrationUpperLimit.getText().trim().matches("-?[0-9]+([.,][0-9]+)?");
            boolean threadCountIsValid = threadCount.getText().trim().matches("[0-9]+");

            if (!(funcIsValid && aIsValid && bIsValid && threadCountIsValid)) {
                function.setStyle("-fx-border-color: " + (funcIsValid ? "#C4C4C4" : "red"));
                integrationLowerLimit.setStyle("-fx-border-color: " + (aIsValid ? "#C4C4C4" : "red"));
                integrationUpperLimit.setStyle("-fx-border-color: " + (bIsValid ? "#C4C4C4" : "red"));
                threadCount.setStyle("-fx-border-color: " + (threadCountIsValid ? "#C4C4C4" : "red"));
                alert(Alert.AlertType.ERROR,
                        "Ошибка",
                        null,
                        "Не корректный ввод\nПроверьте правильность введенных данных");
                return;
            }

            function.setStyle("-fx-border-color: #C4C4C4");
            integrationLowerLimit.setStyle("-fx-border-color: #C4C4C4");
            integrationUpperLimit.setStyle("-fx-border-color: #C4C4C4");
            threadCount.setStyle("-fx-border-color: #C4C4C4");

            CalculationInput input = new CalculationInput();
            input.func = function.getText().trim();
            input.a = Double.parseDouble(integrationLowerLimit.getText().trim().replace(",", "."));
            input.b = Double.parseDouble(integrationUpperLimit.getText().trim().replace(",", "."));
            input.n = 100;
            input.h = (input.b - input.a) / input.n;
            input.threadCount = Integer.parseInt(threadCount.getText());

            String selectedItem = methodChoicer.getSelectionModel().getSelectedItem();
            CalculationOutput output = availableMethods.get(selectedItem).apply(input);
            result.setText("Результат: " + output.result);
            timer.setText("Время работы алгоритма: " + output.time + " мс");

            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            for (double i = input.a; i <= input.b; i += input.h)
                series.getData().add(new XYChart.Data<>(i, Implementations.f(input.func, i)));
            series.setName(function.getText());
            chart.getData().clear();
            chart.getData().add(series);
            chart.getXAxis().setLabel("x");
            chart.getYAxis().setLabel("f(x)");
        });
    }

    private static void alert(Alert.AlertType type, String title, String header, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

}