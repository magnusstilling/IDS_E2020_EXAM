package sample;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.*;


public class Controller{
    //Javafx fields
    public Button buttonTakeOff;
    public Button buttonFlipLeft;
    public Button buttonUp;
    public Button buttonMoveLeft;
    public Button buttonMoveRight;
    public Button buttonMoveForward;
    public Button buttonLand;
    public Button buttonDown;
    public Button buttonRotateLeft;
    public Button buttonRotateRight;
    public Button buttonMoveBack;
    public Button buttonFlipRight;
    public TableColumn tableColumnFromIP;
    public TableColumn tableColumnFromPort;
    public TableColumn tableColumnToIP;
    public TableColumn tableColumnToPort;
    public TableColumn tableColumnASCII;
    public TableColumn tableColumnHEX;
    public Slider sliderAltitude;
    public Canvas canvasCanvas;
    public TableView tableViewColumns;
    public TableColumn tableColumnTime;
    private GraphicsContext graphicsContext;
    private ObservableList<UdpPackage> loggedPackages = FXCollections.observableArrayList();

    //Udp fields
    private UdpPackageReceiver receiver;
    private DatagramSocket sender;
    int toPort = 6000;
    boolean running = false;

    //Drone fields
    double halfWindowWidth = 200;
    double halfWindowHeight = 100;
    double initialDroneWidth = 40;
    double initialDroneHeight = 40;
    double droneAngle = 0;
    int droneAltitude = 0;
    //DroneObject
    private Drone drone = new Drone(halfWindowWidth, halfWindowHeight, initialDroneWidth, initialDroneHeight, droneAngle, droneAltitude);



    public void initialize() throws UnknownHostException {
        // runs when application GUI is ready

        //add list of items to table
        tableViewColumns.setItems(loggedPackages);

        //set columns content
        tableColumnTime.setCellValueFactory(
                new PropertyValueFactory<UdpPackage,String>("formattedDate")
        );
        tableColumnASCII.setCellValueFactory(
                new PropertyValueFactory<UdpPackage, String>("dataAsString")
        );
        tableColumnHEX.setCellValueFactory(
                new PropertyValueFactory<UdpPackage, String>("dataAsHex")
        );
        tableColumnFromPort.setCellValueFactory(
                new PropertyValueFactory<UdpPackage, Integer>("fromPort")
        );
        tableColumnFromIP.setCellValueFactory(
                new PropertyValueFactory<UdpPackage, String>("fromIp")
        );
        tableColumnToPort.setCellValueFactory(
                new PropertyValueFactory<UdpPackage, Integer>("toPort")
        );
        tableColumnToIP.setCellValueFactory(
                new PropertyValueFactory<UdpPackage, String>("toIp")
        );
        tableViewColumns.getSortOrder().add(tableColumnTime);

        //add udp server/receiver for updating table views and receiving UDP packages
        receiver = new UdpPackageReceiver(loggedPackages, toPort, this);
        new Thread(receiver).start();

        //create udp sender
        try {
            sender = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }


        /*Graphics*/

        graphicsContext = canvasCanvas.getGraphicsContext2D();

        //First drawing of the drone
        drone.drawDrone(canvasCanvas);
    }

    /**
     * Method for momentarily indicating which button is pressed, and if it is legal or not
     * @param buttonColorChange Event fired button from the GUI
     * @param isButtonActionLegal 0 = not legal / 1 = legal
     *                            0 shows a red color / 1 shows a green color
     */
    public void setColor(Button buttonColorChange, int isButtonActionLegal) {
        switch(isButtonActionLegal) {
            case 0:
                buttonColorChange.setStyle("-fx-background-color: red;");
                PauseTransition pauseTransition = new PauseTransition(Duration.millis(420));
                pauseTransition.setOnFinished(event -> buttonColorChange.setStyle("-fx-background-color: grey;"));
                pauseTransition.play();
                break;
            case 1:
                buttonColorChange.setStyle("-fx-background-color: green;");
                pauseTransition = new PauseTransition(Duration.millis(420));
                pauseTransition.setOnFinished(event -> buttonColorChange.setStyle("-fx-background-color: grey;"));
                pauseTransition.play();
                break;
        }


    }

    public void takeOff(MouseEvent mouseEvent) throws UnknownHostException, IOException {
        if(!drone.isFlying()) {
            drone.setAltitude(50);
            drone.setWidth(drone.getWidth()); //Doubles the width of the drone image
            drone.setHeight(drone.getHeight()); //Doubles the height of the drone image
            drone.drawDrone(canvasCanvas);
            drone.setFlying(true);
            sliderAltitude.setValue(drone.getAltitude());
            droneCommandSender("takeoff");
            setColor(buttonTakeOff, 1);
        } else {
            setColor(buttonTakeOff, 0);
            System.out.println("Cannot takeOff. Drone is already flying");
        }
    }

    public void moveLeft(MouseEvent mouseEvent) throws UnknownHostException, IOException {
        if(drone.isFlying()) {
            drone.setX(-10);
            drone.drawDrone(canvasCanvas);
            droneCommandSender("left 40");
            setColor(buttonMoveLeft, 1);
        } else{
            setColor(buttonMoveLeft, 0);
            System.out.println("Cannot moveLeft. Drone is not flying");
        }
    }

    public void moveRight(MouseEvent mouseEvent) throws UnknownHostException, IOException {
        if(drone.isFlying()) {
            drone.setX(10);
            drone.drawDrone(canvasCanvas);
            droneCommandSender("right 40");
            setColor(buttonMoveRight, 1);
        } else{
            setColor(buttonMoveRight, 0);
            System.out.println("Cannot moveRight. Drone is not flying");
        }
    }

    public void moveBack(MouseEvent mouseEvent) throws UnknownHostException, IOException {
        if(drone.isFlying()) {
            drone.setY(10);
            drone.drawDrone(canvasCanvas);
            droneCommandSender("back 40");
            setColor(buttonMoveBack, 1);
        } else{
            setColor(buttonMoveBack, 0);
            System.out.println("Cannot moveBack. Drone is not flying");
        }
    }

    public void moveForward(MouseEvent mouseEvent) throws UnknownHostException, IOException {
        if(drone.isFlying()) {
            drone.setY(-10);
            drone.drawDrone(canvasCanvas);
            droneCommandSender("forward 40");
            setColor(buttonMoveForward, 1);
        }else{
            setColor(buttonMoveForward, 0);
            System.out.println("Cannot moveForward. Drone is not flying");
        }
    }

    public void rotateRight(MouseEvent mouseEvent) throws UnknownHostException, IOException {
        if(drone.isFlying()) {
            drone.rotateDrone(canvasCanvas, 45, "right");
            if (drone.getAngle() == 360){
                drone.overWriteAngle(0);
            }
            droneCommandSender("cw 45");
            setColor(buttonRotateRight, 1);
            System.out.println("Drone rotated Right");
        } else{
            setColor(buttonRotateRight, 0);
            System.out.println("Cannot rotateRight. Drone is not flying");
        }
    }

    public void rotateLeft(MouseEvent mouseEvent) throws UnknownHostException, IOException {
        if(drone.isFlying()) {
            drone.rotateDrone(canvasCanvas,45,"left");
            if (drone.getAngle() == -360){
                drone.overWriteAngle(0);
            }
            droneCommandSender("ccw 45");
            setColor(buttonRotateLeft, 1);
            System.out.println("Drone rotated Left");
        } else{
            setColor(buttonRotateLeft, 0);
            System.out.println("Cannot rotateLeft. Drone is not flying");
        }
    }

    public void moveDown(MouseEvent mouseEvent) throws UnknownHostException, IOException {
        if(drone.isFlying() && (sliderAltitude.getValue() > 5)) {
            drone.setAltitude(-5);
            drone.setWidth(-2);
            drone.setHeight(-2);
            drone.drawDrone(canvasCanvas);
            droneCommandSender("down 40");
            sliderAltitude.setValue(drone.getAltitude());
            setColor(buttonDown, 1);
        } else{
            setColor(buttonDown, 0);
            System.out.print("Cannot moveDown. ");
            buttonFire(buttonLand);
        }
    }

    public void moveUp(MouseEvent mouseEvent) throws UnknownHostException, IOException {
        if(drone.isFlying() && (sliderAltitude.getValue() < 100)) {
            drone.setAltitude(5);
            drone.setWidth(2);
            drone.setHeight(2);
            drone.drawDrone(canvasCanvas);
            droneCommandSender("up 40");
            sliderAltitude.setValue(drone.getAltitude());
            setColor(buttonUp, 1);
        } else if(drone.isFlying() && (sliderAltitude.getValue() == 100)){
            setColor(buttonUp, 0);
            System.out.println("WATCH OUT ICARUS!!! THE DRONE IS GETTING TOO CLOSE TO THE SUN!!!");
        } else{
            System.out.println("Cannot moveUp. Drone is not flying");
        }
    }

    public void flipLeft(MouseEvent mouseEvent) throws UnknownHostException, IOException {
        if(drone.isFlying() && (sliderAltitude.getValue() >= 50)){
            drone.setX(-50);
            drone.drawDrone(canvasCanvas);
            droneCommandSender("flip l");
            setColor(buttonFlipLeft, 1);
        } else{
            setColor(buttonFlipLeft, 0);
            System.out.println("Cannot flipLeft. Drone is too close to the ground");
        }
    }

    public void flipRight(MouseEvent mouseEvent) throws UnknownHostException, IOException {
        if(drone.isFlying() && (sliderAltitude.getValue() >= 50)){
            drone.setX(50);
            drone.drawDrone(canvasCanvas);
            droneCommandSender("flip r");
            setColor(buttonFlipRight, 1);
        } else{
            setColor(buttonFlipRight, 0);
            System.out.println("Cannot flipRight. Drone is too close to the ground");
        }
    }

    public void land(MouseEvent mouseEvent) throws UnknownHostException, IOException {
        if(drone.isFlying()) {
            drone.overWriteAltitude(0);
            drone.overWriteWidth(initialDroneWidth);
            drone.overWriteHeight(initialDroneHeight);
            drone.drawDrone(canvasCanvas);
            sliderAltitude.setValue(drone.getAltitude());
            drone.setFlying(false);
            droneCommandSender("land");
            setColor(buttonLand, 1);
        } else{
            setColor(buttonLand, 0);
            System.out.println("Drone is not flying");
        }
    }

    public void droneCommandSender(String command) throws UnknownHostException, IOException {
        InetAddress localIP = InetAddress.getByName("127.0.0.1");
        InetAddress droneIP = InetAddress.getByName("192.168.10.1");
        int localPort = 6000;
        int dronePort = 8889;

        UdpPackage takeOffPackage = new UdpPackage(command, localIP, droneIP, localPort,dronePort);
        sender.send(new DatagramPacket(takeOffPackage.getDataAsBytes(), takeOffPackage.getDataAsBytes().length, droneIP, dronePort));
        loggedPackages.addAll(takeOffPackage);
        tableViewColumns.getSortOrder().add(tableColumnTime);
    }

    public void buttonFire(Button buttonToFire){
        Event.fireEvent(buttonToFire, new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                true, true, true, true, true, true, null));
    }

    public void getDataFromUdpPackageReceiver(String msg) {
        System.out.println(msg + "..");
        if (msg.substring(0,7).equals("takeoff")){
            buttonFire(buttonTakeOff);
        }else if (msg.substring(0,7).equals("left 40")){
            buttonFire(buttonMoveLeft);
        }else if (msg.substring(0,8).equals("right 40")){
            buttonFire(buttonMoveRight);
        }else if (msg.substring(0,7).equals("back 40")){
            buttonFire(buttonMoveBack);
        }else if (msg.substring(0,10).equals("forward 40")){
            buttonFire(buttonMoveForward);
        }else if (msg.substring(0,5).equals("cw 45")){
            buttonFire(buttonRotateRight);
        }else if (msg.substring(0,6).equals("ccw 45")){
            buttonFire(buttonRotateLeft);
        }else if (msg.substring(0,7).equals("down 40")){
            buttonFire(buttonDown);
        }else if (msg.substring(0,5).equals("up 40")){
            buttonFire(buttonUp);
        }else if (msg.substring(0,6).equals("flip l")){
            buttonFire(buttonFlipLeft);
        }else if (msg.substring(0,6).equals("flip r")){
            buttonFire(buttonFlipRight);
        }else if (msg.substring(0,4).equals("land")){
            buttonFire(buttonLand);
        }else {
            System.out.println("Unknown command");
        }
        tableViewColumns.getSortOrder().add(tableColumnTime);
    }
}
