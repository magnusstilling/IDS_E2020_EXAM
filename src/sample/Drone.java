package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Drone {

    private double x, y, width, height, angle;
    private double rotationCenterX;
    private double rotationCenterY;
    private int altitude;
    private boolean flying;
    Image droneImage = new Image("sample/dronePNG.png");

    public Drone(double x, double y, double width, double height, double angle, int altitude) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.angle = angle;
        this.altitude = altitude;
        this.rotationCenterX = 0;
        this.rotationCenterY = 0;
        this.flying = false;
    }

    //X
    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x += x;
        System.out.println("new x-value: " + this.x);
    }
    public void overWriteX(double i){
        this.x = i;
        System.out.println("X was overwritten. X is now: " + this.x);
    }

    //Y
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y += y;
        System.out.println("new y-value: " + this.y);
    }
    public void overWriteY(double i){
        this.y = i;
        System.out.println("Y was overwritten. Y is now: " + this.y);
    }

    //Width
    public double getWidth() {
        return width;
    }
    public void setWidth(double width) {
        this.width += width;
        System.out.println("new width: " + this.width);
    }
    public void overWriteWidth(double i){
        this.width = i;
        System.out.println("Width was overWritten. Width is now: " + this.width);
    }

    //Height
    public double getHeight() {
        return height;
    }
    public void setHeight(double height) {
        this.height += height;
        System.out.println("new height: " + this.height);
    }
    public void overWriteHeight(double i){
        this.height = i;
        System.out.println("Height was overWritten. Height is now: " + this.height);
    }

    //Angle
    public double getAngle() {
        return angle;
    }
    public void setAngle(double angle) {
        this.angle += angle;
        System.out.println("new angle: " + this.angle);
    }
    public void overWriteAngle(double i){
        this.angle = i;
    }

    //Altitude
    public int getAltitude() {
        return altitude;
    }
    public void setAltitude(int altitude) {
        this.altitude += altitude;
        System.out.println("new altitude: " + this.altitude);
    }
    public void overWriteAltitude(int i){
        this.altitude = i;
        System.out.println("Altitude was overwritten. Altitude is now: " + this.altitude);
    }

    //Flying?
    public void setFlying(Boolean b){
        this.flying = b;
        System.out.println("Drone is flying: " + this.flying);
    }
    public boolean isFlying(){
        return this.flying;
    }

    public void drawDrone(Canvas canvasCanvas){
        GraphicsContext droneShape = canvasCanvas.getGraphicsContext2D();
        droneShape.clearRect(0, 0, canvasCanvas.getWidth(), canvasCanvas.getHeight());
        droneShape.drawImage(droneImage, this.x, this.y, this.width, this.height);
    }

    public void rotateDrone(Canvas canvasCanvas, int angle, String leftOrRight){
        this.rotationCenterX = (this.getX() + (this.getWidth()/2));
        this.rotationCenterY = (this.getY() + (this.getHeight()/2));
        if(leftOrRight.equalsIgnoreCase("Left")){
            angle = angle *-1;
            this.setAngle(angle);
        } else if(leftOrRight.equalsIgnoreCase("Right")){
            this.setAngle(angle);
        }

        GraphicsContext droneShape = canvasCanvas.getGraphicsContext2D();
        droneShape.clearRect(0, 0, canvasCanvas.getWidth(), canvasCanvas.getHeight());
        droneShape.translate(this.rotationCenterX, this.rotationCenterY);
        droneShape.rotate(angle);
        droneShape.translate(-this.rotationCenterX, -this.rotationCenterY);
        droneShape.drawImage(droneImage, this.x, this.y, this.width, this.height);

    }

    public void flipDrone(Canvas canvasCanvas, String leftOrRight){
        //Måske java animations?
    }
}
