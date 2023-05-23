module phestrix.factoryapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires lombok;
    requires org.apache.log4j;


    opens phestrix.factoryapplication to javafx.fxml;
    exports phestrix.factoryapplication;
    opens phestrix.Factory.GUI.FXMLS to javafx.fxml;
    exports phestrix.Factory.GUI.FXMLS;


}