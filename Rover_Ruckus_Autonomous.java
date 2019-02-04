package org.firstinspires.ftc.teamcode;

/**
* Created by Nate on 9/13/18.
*/

/*
Key:
flw = front left wheel
frw = front right wheel
blw = back left wheel
brw = back right wheel
*/

/*
                       Playing field

       ------------------------------------------------------------
       |                                                          |
       |  Blue Crater                        Red Claiming point   |
       |                                                          |
       |                                                          |
       |                                                          |
       |                                                          |
       |                  Pos 2        Pos 1                      |
       |                        Lander                            |
       |                  Pos 1        Pos 2                      |
       |                                                          |
       |                                                          |
       |                                                          |
       |                                                          |
       |                                                          |
       | Blue Claiming point                        Red Crater    |
       |                                                          |
       ------------------------------------------------------------

*/

//imports
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
//imports

@Autonomous(name="Rover_Ruckus_Autonomous", group="Shockwave")

public class Rover_Ruckus_Autonomous extends LinearOpMode {

   //Defining the wheels thier motors
   DcMotor Front_left_wheel;
   DcMotor Front_right_wheel;
   DcMotor Back_left_wheel;
   DcMotor Back_right_wheel;

   //Arm
   DcMotor shoulder;
   DcMotor bicep;

   //gyro or imu
   // The IMU sensor object
   BNO055IMU imu;

   // State used for updating telemetry
   Orientation angles;

   //Wheel stats
   static final int motor_revcount = 288; //288 ticks per rotation
   static final double wheel_diameter = 3.54;
   static final double counts_per_inch = (motor_revcount) / (wheel_diameter * Math.PI);

   //forearm stats
   static final int forearm_revcount = 280; //280 ticks per rotation
   static final double gear_diameter = 0.6; // inches
   static final double arm_counts_per_inch = (forearm_revcount) / (gear_diameter * 3.1415);

   //Speed stats
   static double drive_speed = 1.0;
   static double top_speed = 1.0;
   static double bottom_speed = 0.2;

   //A little forgiveness for the robot
   static double tolerance = 5;

   //Slows down before finding correct angle to prvent the robot from over shoting it
   static int almost_there = 20;

   //distance between the game pieces
   static double distance_between_the_game_pieces = 16.970562748477105822026;

   //where is the game piece
   public String gp = "null";

   //Has the pos_inputed
   String pos_inputed = "not inputed";

   //Red or blue?
   String red_or_blue = "not inputed";

   //Tenser flow
   public static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";

   //gold
   public static final String LABEL_GOLD_MINERAL = "Gold Mineral";

   //silver
   public static final String LABEL_SILVER_MINERAL = "Silver Mineral";


   //Vuforia key
   public static final String VUFORIA_KEY = "ASB+lMn/////AAABmSELZ/IC5U5BklpoBPC3MN4Bnga4Xi/C52uy24x/iu+1isv802qak3Cz7DOfClTCJ0WCslWLoV5ClMI7bZ0r9g6woGi76wVh5QT5qCojPJAWQCI/mWexTqyZRHLEsB9i6bVvI6aP2fAOCOy5W2+MkXZ8ATQQXmpCkiCNIM7uijcc1KUbnInxxZwKC249md7EOb8jgY/I0zflVOeMj2wi/SS/V98KfZcDbis0DeMBhOtG3YiNwr9Ak5Zo5rutC5V8gCiJTRQm9acUmHoj1h18klkoRu4yt97kJnoemgnbrbzWiPJZ1iWwGNcFruZSvtCxApi4vMk+H//LZztLJnnpXFejWjVaJL+s437tIFp8w/aV";

   //Vuforia: the variable we will use to store our instance of the Vuforia
   public VuforiaLocalizer vuforia;

   //Tfod is the variable we will use to store our instance of the Tensor Flow Object
   public TFObjectDetector tfod;

   public void where_are_we(){

       //Color message
       telemetry.addData("Please", " input color. Press x if we are Blue and press B if we are red.");
       telemetry.update();

       while (red_or_blue == "not inputed"){
           if (gamepad1.x){
               red_or_blue = "Blue";
           }

           if (gamepad1.b){
               red_or_blue = "Red";
           }
       }

       //Position message
       telemetry.addData("Please", " input position. Press Y if we are in position 1" +
               " and press A if we are position 2.");
       telemetry.update();

       while (pos_inputed == "not inputed"){
           if (gamepad1.y){
               pos_inputed = "1";
           }

           if (gamepad1.a){
               pos_inputed = "2";
           }
       }
   }

   public void initVuforia() {
       /*
        * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
        */
       VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

       parameters.vuforiaLicenseKey = VUFORIA_KEY;
       parameters.cameraDirection = CameraDirection.BACK;

       //  Instantiate the Vuforia engine
       vuforia = ClassFactory.getInstance().createVuforia(parameters);

       // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
   }

   public void initTfod() {
       int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
               "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
       TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
       tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
       tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
   }

   public void find_angle(double angle_I_want) {

       boolean finding_angle = true;

       while (finding_angle == true) {

           //finds angles
           angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

           //The bigger this is the closer it has to be to slow down
           double multiplier = 1;
           double angle_diff;

           /*
           angles.firstAngle = xy plane
           angles.secondAngle = tilt
           angles.thirdAngle = roll
           */

           if (Math.round(angles.firstAngle) > angle_I_want) {

               //Slows down before finding correct angle to prevent the robot from over shoting it
               angle_diff = (angles.firstAngle - angle_I_want);
               drive_speed = Math.min(Math.max((angle_diff * multiplier), top_speed), bottom_speed);

               Front_left_wheel.setPower(-drive_speed);
               Front_right_wheel.setPower(-drive_speed);
               Back_left_wheel.setPower(-drive_speed);
               Back_right_wheel.setPower(-drive_speed);

           } else if (Math.round(angles.firstAngle) < angle_I_want) {

               //Slows down before finding correct angle to prevent the robot from over shoting it
               angle_diff = (angle_I_want - angles.firstAngle);
               drive_speed = Math.min(Math.max((angle_diff * multiplier), top_speed), bottom_speed);

               Front_left_wheel.setPower(drive_speed);
               Front_right_wheel.setPower(drive_speed);
               Back_left_wheel.setPower(drive_speed);
               Back_right_wheel.setPower(drive_speed);

           }

           else {

               //Tells user the robot found the right angle
               telemetry.addData("On", "track!!!");
               telemetry.update();

               //Stops all motion
               Front_left_wheel.setPower(0);
               Front_right_wheel.setPower(0);
               Back_left_wheel.setPower(0);
               Back_right_wheel.setPower(0);

               //FOUND ANGLE :)
               finding_angle = false;

           }

           //Updates user
           telemetry.addData("First angle is is '%s'", angles.firstAngle);
           telemetry.update();
       }

   }//end of find angle

   //Move Arm
   public void use_arm(int inches, int forearm_power, boolean claw_open){

       int new_forearm_target;
       int new_bicep_target;

       telemetry.addData("Current position:",shoulder.getCurrentPosition());
       telemetry.update();

       if (opModeIsActive()) {

           //target
           new_forearm_target = shoulder.getCurrentPosition() + (int)(inches * arm_counts_per_inch);
           shoulder.setTargetPosition(new_forearm_target);

           while (opModeIsActive() && (shoulder.isBusy())) {

               shoulder.setPower(forearm_power);
               // Display it for the driver.
               telemetry.addData("forearm: ", shoulder.getCurrentPosition());
               telemetry.update();
           }

           //stop!
           shoulder.setPower(0);

       }//Opmode is active

       telemetry.addData("Current position:",shoulder.getCurrentPosition());
       telemetry.update();

       //stop
       shoulder.setPower(0.0);

   }//End of move arm

   public void drive(double Front_left_wheel_power,double Front_right_wheel_power ,double Back_left_wheel_power ,double Back_right_wheel_power , double inches){

       //needed for positioning
       int new_flw_target;
       int new_frw_target;
       int new_blw_target;
       int new_brw_target;

       if (opModeIsActive()) {

           //flw target
           new_flw_target = shoulder.getCurrentPosition() + (int)(inches * arm_counts_per_inch);
           Front_left_wheel.setTargetPosition(new_flw_target);

           //frw target
           new_frw_target = shoulder.getCurrentPosition() + (int)(inches * arm_counts_per_inch);
           Front_right_wheel.setTargetPosition(new_frw_target);

           //blw target
           new_blw_target = shoulder.getCurrentPosition() + (int)(inches * arm_counts_per_inch);
           Back_left_wheel.setTargetPosition(new_blw_target);

           //brw target
           new_brw_target = shoulder.getCurrentPosition() + (int)(inches * arm_counts_per_inch);
           Back_right_wheel.setTargetPosition(new_brw_target);

           //finds angles
           angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

           //Determines which varibles power each motor
           Front_left_wheel.setPower(Front_left_wheel_power);
           Front_right_wheel.setPower(Front_right_wheel_power);
           Back_left_wheel.setPower(Back_left_wheel_power);
           Back_right_wheel.setPower(Back_right_wheel_power);

           // Turn On RUN_TO_POSITION
           Front_left_wheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           Front_right_wheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           Back_left_wheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           Back_right_wheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);

           while (opModeIsActive() && Front_left_wheel.isBusy() && Front_left_wheel.isBusy() && Back_left_wheel.isBusy() && Back_right_wheel.isBusy()){

               Front_left_wheel.setPower(Front_left_wheel_power);
               Front_right_wheel.setPower(Front_right_wheel_power);
               Back_left_wheel.setPower(Back_left_wheel_power);
               Back_right_wheel.setPower(Back_right_wheel_power);

           }

       }//Opmode is active

   }//End of drive

   public void angle_drive(double Front_left_wheel_power,double Front_right_wheel_power ,double Back_left_wheel_power ,double Back_right_wheel_power , double inches, double angle){

       //goes to angle
       find_angle(angle);

       //needed for positioning
       int new_flw_target;
       int new_frw_target;
       int new_blw_target;
       int new_brw_target;

       if (opModeIsActive()) {

           //flw target
           new_flw_target = shoulder.getCurrentPosition() + (int)(inches * arm_counts_per_inch);
           Front_left_wheel.setTargetPosition(new_flw_target);

           //frw target
           new_frw_target = shoulder.getCurrentPosition() + (int)(inches * arm_counts_per_inch);
           Front_right_wheel.setTargetPosition(new_frw_target);

           //blw target
           new_blw_target = shoulder.getCurrentPosition() + (int)(inches * arm_counts_per_inch);
           Back_left_wheel.setTargetPosition(new_blw_target);

           //brw target
           new_brw_target = shoulder.getCurrentPosition() + (int)(inches * arm_counts_per_inch);
           Back_right_wheel.setTargetPosition(new_brw_target);

           //finds angles
           angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

           //Determines which varibles power each motor
           Front_left_wheel.setPower(Front_left_wheel_power);
           Front_right_wheel.setPower(Front_right_wheel_power);
           Back_left_wheel.setPower(Back_left_wheel_power);
           Back_right_wheel.setPower(Back_right_wheel_power);

           // Turn On RUN_TO_POSITION
           Front_left_wheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           Front_right_wheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           Back_left_wheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           Back_right_wheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);

           //if shifted find angle
           if ((angle > Math.round(angles.firstAngle) + tolerance) || (angle < Math.round(angles.firstAngle) - tolerance)) {
               //goes to angle
               find_angle(angle);

           }

           while (opModeIsActive() && Front_left_wheel.isBusy() && Front_left_wheel.isBusy() && Back_left_wheel.isBusy() && Back_right_wheel.isBusy()){

               Front_left_wheel.setPower(Front_left_wheel_power);
               Front_right_wheel.setPower(Front_right_wheel_power);
               Back_left_wheel.setPower(Back_left_wheel_power);
               Back_right_wheel.setPower(Back_right_wheel_power);

           }

       }//Opmode is active

   }//End of angle_drive

   //find gold method
   public void find_gold(){

       boolean finding_gold = true;

       //loop
       if (opModeIsActive()) {

           /** Activate Tensor Flow Object Detection. */
           if (tfod != null) {
               tfod.activate();
           }

           while (finding_gold) {

               if (tfod != null) {

                   // getUpdatedRecognitions() will return null if no new information is available since
                   // the last time that call was made.
                   List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();

                   //checks if updatedRecognitions is not null
                   if (updatedRecognitions != null) {

                       //Found 3 of something
                       if (updatedRecognitions.size() == 3) {
                           telemetry.addData("# Object Detected", updatedRecognitions.size());
                           telemetry.update();

                           int goldx = -1;
                           int silver1x = -1;
                           int silver2x = -1;

                           //Recognition
                           for (Recognition recognition : updatedRecognitions) {

                               if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {

                                   goldx = (int) recognition.getLeft();

                               } else if (silver1x == -1) {

                                   silver1x = (int) recognition.getLeft();

                               } else {

                                   silver2x = (int) recognition.getLeft();
                               }

                               //checks to see which is more left
                               if (goldx != -1 && silver1x != -1 && silver2x != -1) {

                                   //Gold is on the left
                                   if (goldx < silver1x && goldx < silver2x) {
                                       telemetry.addData("Gold Mineral Position", "Left");
                                       telemetry.update();

                                       gp = "Left";

                                       //found it
                                       finding_gold = false;

                                   }

                                   //Gold is on the right
                                   if (goldx > silver1x && goldx > silver2x) {
                                       telemetry.addData("Gold Mineral Position", "Right");
                                       telemetry.update();

                                       gp = "Right";

                                       //found it
                                       finding_gold = false;

                                   }

                                   //Gold is center
                                   else {
                                       telemetry.addData("Gold Mineral Position", "Center");
                                       telemetry.update();

                                       gp = "Center";

                                       //found it
                                       finding_gold = false;

                                   }


                               }//checks to see which is more left

                           }//Recognition

                       }//Found 3 of something

                   }//checks if updatedRecognitions is not null

               }//tfod != null

           }//while opmode is active
       }

       //Turns off camera
       if (tfod != null) {
           tfod.shutdown();
       }


   }//End of find gold

   @Override
   //Where all the commands are issued------------------------------------------------------------------------------------
   public void runOpMode() {

       //Getting the wheels ready------------------------------------------------------------------

       //Finds the wheels
       Front_left_wheel = hardwareMap.dcMotor.get("Front_left_wheel");
       Front_right_wheel = hardwareMap.dcMotor.get("Front_right_wheel");
       Back_left_wheel = hardwareMap.dcMotor.get("Back_left_wheel");
       Back_right_wheel = hardwareMap.dcMotor.get("Back_right_wheel");

       //reverse backwards motor
       Front_left_wheel.setDirection(DcMotor.Direction.REVERSE);
       Front_right_wheel.setDirection(DcMotor.Direction.REVERSE);

       //Arm
       shoulder = hardwareMap.dcMotor.get("Arm");
       bicep = hardwareMap.dcMotor.get("bicep");

       //Getting the wheels ready------------------------------------------------------------------

       //Getting the gyro ready--------------------------------------------------------------------

       BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
       parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
       parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
       parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode

       //Gyro
       imu = hardwareMap.get(BNO055IMU.class, "imu");
       imu.initialize(parameters);

       // Start the logging of measured acceleration
       imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);

       //Getting the gyro ready--------------------------------------------------------------------

       //initilized Vuforia
       initVuforia();

       //Checks if the damn thing can use the tenser flow
       if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
           initTfod();
       } else {
           telemetry.addData("Sorry!", "This device is not compatible with TFOD");
       }

       //Driver input
       where_are_we();

       //wait for start
       waitForStart();

       //find gold
       find_gold();

       //goes up
       use_arm(10,1,false);

       //goes left
       drive(-1,1,1,-1,1);

       //goes down
       use_arm(10,-1,false);

       //get in front of blocks
       drive(1,1,1,1,5);

       //gold------------------------------------------------------------------------------------------------------------------------------------------------

//gold is left
if (gp.matches("Left")){

   //go to center
   drive(-1,1,1,-1, distance_between_the_game_pieces);

   //drive forward
   drive(1,1,1,1, 3);

}

//gold is right
if (gp.matches("Right")){

   //go to center
   drive(1,-1,-1,1, distance_between_the_game_pieces);

   //drive forward
   drive(1,1,1,1, 3);
}

//gold is center
if (gp.matches("Center")){

   //drive forward
   drive(1,1,1,1, 3);

}

//gold------------------------------------------------------------------------------------------------------------------------------------------------


       if(red_or_blue.matches("Red")){

           if (pos_inputed.matches("1")){

               //turn right 120 degrees
               angle_drive(1,-1,1,-1,20, 120);

 //go into crator
 drive(1,1,1,1,20);


           }

           //go into crator
           if (pos_inputed.matches("2")){

               //away from lander
               drive(1,1,1,1,10);

           }

       }

       if(red_or_blue.matches("Blue")){

           if (pos_inputed.matches("1")){

               //turn left 120 degrees
               angle_drive(-1,1,-1,1,20, 120);

               //go into crator
     		 drive(1,1,1,1,20);


           }

           //go into crator
           if (pos_inputed.matches("2")){

               //away from lander
               drive(1,1,1,1,10);

           }

       }

   }//End of op mode
   //Where all the commands are issued------------------------------------------------------------------------------------

}//End of class

