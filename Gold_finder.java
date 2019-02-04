package org.firstinspires.ftc.teamcode;

//imports

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

//imports

//@Disabled

//Tells the program where to go
@TeleOp(name="Gold_finder", group="Shockwave")

public class Gold_finder extends LinearOpMode {

   //wheel stats -------------------------------------------------------------------------------------

   //Defining the wheels thier motors
   DcMotor Front_left_wheel;
   DcMotor Front_right_wheel;
   DcMotor Back_left_wheel;
   DcMotor Back_right_wheel;

   //Core hex motor
   static final double COUNTS_PER_MOTOR_REV = 2240 ;

   static final double DRIVE_GEAR_REDUCTION = 2.0 ;

   //For figuring out circumference
   static final double WHEEL_DIAMETER_INCHES  = 4.5;

   static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);

   static final double DRIVE_SPEED = 0.6;

   static final double TURN_SPEED  = 0.5;

   //wheel stats -------------------------------------------------------------------------------------

   //distacne between the game pieces
   static final double distance_between_the_game_pieces = 16.970562748477105822026;

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

   public void drive(double front_left_speed, double front_right_speed, double back_left_speed, double back_right_speed, double inches){

       int Front_left_wheel_new_target;
       int Front_right_wheel_new_target;
       int Back_left_wheel_new_target;
       int Back_right_wheel_new_target;

       //Opmode is active
       if (opModeIsActive()) {

           //New target
           Front_left_wheel_new_target = Front_left_wheel.getCurrentPosition() + (int)(inches * COUNTS_PER_INCH);
           Front_right_wheel_new_target = Front_right_wheel.getCurrentPosition() + (int)(inches * COUNTS_PER_INCH);
           Back_left_wheel_new_target = Back_left_wheel.getCurrentPosition() + (int)(inches * COUNTS_PER_INCH);
           Back_right_wheel_new_target = Back_right_wheel.getCurrentPosition() + (int)(inches * COUNTS_PER_INCH);

           //Sets target
           Front_left_wheel.setTargetPosition(Front_left_wheel_new_target);
           Front_right_wheel.setTargetPosition(Front_right_wheel_new_target);
           Back_left_wheel.setTargetPosition(Back_left_wheel_new_target);
           Back_right_wheel.setTargetPosition(Back_right_wheel_new_target);

           // Turn On RUN_TO_POSITION
           Front_left_wheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           Front_right_wheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           Back_left_wheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           Back_right_wheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);

           // reset the timeout time and start motion.
           Front_left_wheel.setPower(Math.abs(front_left_speed));
           Front_right_wheel.setPower(Math.abs(front_right_speed));
           Back_left_wheel.setPower(Math.abs(back_left_speed));
           Back_right_wheel.setPower(Math.abs(back_right_speed));

           //loop
           while(Front_left_wheel.isBusy() && Front_right_wheel.isBusy() && Back_left_wheel.isBusy() && Back_right_wheel.isBusy()){

           }//loop

           // Stop all motion
           Front_left_wheel.setPower(0);
           Front_right_wheel.setPower(0);
           Back_left_wheel.setPower(0);
           Back_right_wheel.setPower(0);

           // Turn off RUN_TO_POSITION
           Front_left_wheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
           Front_right_wheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
           Back_left_wheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
           Back_right_wheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

       }//Opmode is active

   }

   public void runOpMode() {

       //Finds the wheels
       Front_left_wheel = hardwareMap.dcMotor.get("Front_left_wheel");
       Front_right_wheel = hardwareMap.dcMotor.get("Front_right_wheel");
       Back_left_wheel = hardwareMap.dcMotor.get("Back_left_wheel");
       Back_right_wheel = hardwareMap.dcMotor.get("Back_right_wheel");

       //Reverse the wheels
       Front_left_wheel.setDirection(DcMotor.Direction.REVERSE);
       Back_left_wheel.setDirection(DcMotor.Direction.REVERSE);

       //reset
       Front_left_wheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       Front_right_wheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       Back_left_wheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       Back_right_wheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

       // Turn On RUN_TO_POSITION
       Front_left_wheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
       Front_right_wheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
       Back_left_wheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
       Back_right_wheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);

       //initilized Vuforia
       initVuforia();

       //Checks if
       if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
           initTfod();
       } else {
           telemetry.addData("Sorry!", "This device is not compatible with TFOD");
       }

       //Ready to go
       telemetry.addData("Gold", " finder ready!");
       telemetry.update();

       //Waiting
       waitForStart();

       //loop
       if (opModeIsActive()) {

           /** Activate Tensor Flow Object Detection. */
           if (tfod != null) {
               tfod.activate();
           }

           //opmode is active
           while (opModeIsActive()) {

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

                                       drive(-1,1,1,-1, distance_between_the_game_pieces);

                                   }

                                   //Gold is on the right
                                   if (goldx > silver1x && goldx > silver2x) {
                                       telemetry.addData("Gold Mineral Position", "Right");
                                       telemetry.update();
                                       drive(1,-1,-1,1, distance_between_the_game_pieces);
                                   }

                                   //Gold is center
                                   else {
                                       telemetry.addData("Gold Mineral Position", "Center");
                                       telemetry.update();
                                       drive(1,1,1,1,3);
                                   }


                               }//checks to see which is more left

                           }//Recognition

                       }//Found 3 of something

                   }//checks if updatedRecognitions is not null

               }//tfod != null

           }//while opmode is active

           //turns off the tensor flow object
           if (tfod != null) {
           tfod.shutdown();
           }

       }//if opmode is active

   }//run opmode

}//end of Gold_finder
