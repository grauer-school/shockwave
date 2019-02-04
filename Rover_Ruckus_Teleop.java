package org.firstinspires.ftc.teamcode;

//imports-------------------------------------------------------------------------------------------
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
//imports-------------------------------------------------------------------------------------------

//@Disabled

@TeleOp(name = "Rover_Ruckus_Teleop", group = "Shockwave")

public class Rover_Ruckus_Teleop extends OpMode{

   //wheels
   private DcMotor right_back_motor;  // 1
   private DcMotor left_back_motor;   // 2
   private DcMotor right_front_motor; // 3
   private DcMotor left_front_motor;  // 0

   //arms
   DcMotor shoulder;
   DcMotor bicep;

   //fingers
   DcMotor finger;

   //wheel power
   private double right_back_motor_power = 0;
   private double left_back_motor_power = 0;
   private double right_front_motor_power = 0;
   private double left_front_motor_power = 0;

   //arm power
   public double shoulder_power = 0;
   public double bicep_power = 0;

   //Finger power
   public double finger_power = 1;

   //gyro or imu
   // The IMU sensor object
   //BNO055IMU imu;

   // State used for updating telemetry
   //Orientation angles;

   //Needed for dpad
   public boolean manual_drive;
   public int auto_drive = 1;


   @Override
   public void init() {

       //Sets wheel names
       right_back_motor = hardwareMap.dcMotor.get("right_back_motor");
       left_back_motor = hardwareMap.dcMotor.get("left_back_motor");
       right_front_motor = hardwareMap.dcMotor.get("right_front_motor");
       left_front_motor = hardwareMap.dcMotor.get("left_front_motor");

       //Sets arm names
       shoulder = hardwareMap.dcMotor.get("shoulder");
       bicep = hardwareMap.dcMotor.get("bicep");

       //fingers
       finger = hardwareMap.dcMotor.get("fingers");

       //arm power
       shoulder.setPower(shoulder_power);
       bicep.setPower(bicep_power);

     /*
     when the controls are set to full forward, the robot should go forward.
     If it doesn't, reverse the relevant motors here:
      */
       left_back_motor.setDirection(DcMotor.Direction.REVERSE);
       left_front_motor.setDirection(DcMotor.Direction.REVERSE);

     /*
     we want the drive motors to STOP when we take our hands off the controls,
     so set that behaviour here:
      */

       right_back_motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       left_back_motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       right_front_motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       left_front_motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       shoulder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       bicep.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       finger.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

       //finds angles
       //angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);


       telemetry.addData("Status", "Initialized");
       telemetry.update();

   }

   @Override
   public void init_loop() {
       telemetry.addData("Ready", " to go!");
       telemetry.update();
   }

   @Override
   public void loop() {

       double joyTop = 1.0; // 0 < JoyTop <= 1 : to change max speed of robot

     /* Calculate the power level for each motor.
        The x axis and the y axis are always inversely proportional to each other.
        Forward: all motors forward
        Backward: all motors backward
        Translate right: left-front forward
                         right-rear forward
                         right-front backward
                         left-rear backward
        Translate left:  left-front backward
                         right-rear backward
                         right-front forward
                         left-rear backward
      */

       //Driving controls (defaults to game controller 1)
       if (gamepad1.left_stick_x != 0 || gamepad1.left_stick_y != 0 || gamepad1.right_stick_x != 0 || gamepad1.right_stick_y != 0) {
           right_back_motor_power = -joyTop * gamepad1.right_stick_y + gamepad1.right_stick_x;
           left_back_motor_power = -joyTop * gamepad1.left_stick_y - gamepad1.left_stick_x;
           right_front_motor_power = -joyTop * gamepad1.right_stick_y - gamepad1.right_stick_x;
           left_front_motor_power = -joyTop * gamepad1.left_stick_y + gamepad1.left_stick_x;
       }
       else {
           right_back_motor_power = -joyTop * gamepad2.right_stick_y + gamepad2.right_stick_x;
           left_back_motor_power = -joyTop * gamepad2.left_stick_y - gamepad2.left_stick_x;
           right_front_motor_power = -joyTop * gamepad2.right_stick_y - gamepad2.right_stick_x;
           left_front_motor_power = -joyTop * gamepad2.left_stick_y + gamepad2.left_stick_x;
       }

       if (right_back_motor_power + left_back_motor_power + right_front_motor_power + left_front_motor_power != 0) manual_drive = true;

       //Sets power
       if (manual_drive) {
           right_back_motor.setPower(right_back_motor_power);
           left_back_motor.setPower(left_back_motor_power);
           right_front_motor.setPower(right_front_motor_power);
           left_front_motor.setPower(left_front_motor_power);
       }

       //If the dpad is pressed up it goes backward
       if (gamepad1.dpad_down || gamepad2.dpad_down){
           //find_angle(90);
           right_back_motor.setPower(-auto_drive);
           left_back_motor.setPower(-auto_drive);
           right_front_motor.setPower(-auto_drive);
           left_front_motor.setPower(-auto_drive);
           manual_drive = false;
       }

       //If the dpad is pressed up it goes forward
       if (gamepad1.dpad_up || gamepad2.dpad_up){
           //find_angle(90);
           right_back_motor.setPower(auto_drive);
           left_back_motor.setPower(auto_drive);
           right_front_motor.setPower(auto_drive);
           left_front_motor.setPower(auto_drive);
           manual_drive = false;
       }

       //If the dpad is pressed left it goes left
       if (gamepad1.dpad_left || gamepad2.dpad_left){
           right_back_motor.setPower(-auto_drive);
           left_back_motor.setPower(auto_drive);
           right_front_motor.setPower(auto_drive);
           left_front_motor.setPower(-auto_drive);
           manual_drive = false;
       }

       //If the dpad is pressed right it goes right
       if (gamepad1.dpad_right || gamepad2.dpad_right){
           right_back_motor.setPower(auto_drive);
           left_back_motor.setPower(-auto_drive);
           right_front_motor.setPower(-auto_drive);
           left_front_motor.setPower(auto_drive);
           manual_drive = false;
       }

       telemetry.update();

       //Driving controls----------------------------------------------------------------------------

       //Arm-----------------------------------------------------------------------------------------

       //first controller
       if (gamepad1.left_trigger != 0 || gamepad1.right_trigger != 0) {

           bicep.setPower(joyTop * (gamepad1.right_trigger - gamepad1.left_trigger));

       }

       //second controller
       if(gamepad2.left_trigger != 0 || gamepad2.right_trigger != 0){

           bicep.setPower(joyTop * (gamepad2.right_trigger - gamepad2.left_trigger));

       }

       //stopped controller
       if ( (gamepad1.left_trigger == 0 && gamepad1.right_trigger == 0) && (gamepad2.left_trigger == 0 && gamepad2.right_trigger == 0) ) {

           bicep.setPower(0);

       }

       //up
       if (gamepad1.left_bumper == true || gamepad2.left_bumper == true){

           int foo = -1;
           shoulder.setPower(foo);

       }

       //down
       if (gamepad1.right_bumper == true || gamepad2.right_bumper == true){

           int bar = 1;
           shoulder.setPower(bar);

       }

       //stop
       if ( (gamepad1.right_bumper == false) && (gamepad1.left_bumper == false) && (gamepad2.right_bumper == false) && (gamepad2.left_bumper == false)
               ){

           int zero = 0;
           shoulder.setPower(zero);

       }


       telemetry.addData("shoulder_power: ",  shoulder_power);
       telemetry.addData("bicep_power: ",  /*(joyTop * (gamepad1.left_trigger - gamepad1.right_trigger))*/ "IDK");
       telemetry.addData("forearm_pos: ", shoulder.getCurrentPosition());
       telemetry.addData("bicep_pos", bicep.getCurrentPosition());
       telemetry.update();

       //Arm-----------------------------------------------------------------------------------------

       //Fingers------------------------------------------------------------------------
       //out
       if (gamepad1.y || gamepad2.y){
           finger.setPower(finger_power);
       } else if (gamepad1.a || gamepad2.a){
           finger.setPower(-finger_power);
       }
/*
       //out
       if (gamepad1.y){
           finger.setPower(finger_power);
       }

       //in
       if (gamepad1.a){
           finger.setPower(-finger_power);
       }
*/
       //stop
       if (gamepad1.b || gamepad2.b){
           finger.setPower(0);
       }
       //Fingers-------------------------------------------------------------------------

   }  // end of public void loop

   @Override
   public void stop() {

       //stop value
       int stop = 0;

       right_back_motor.setPower(stop);
       left_back_motor.setPower(stop);
       right_front_motor.setPower(stop);
       left_front_motor.setPower(stop);
       shoulder.setPower(stop);
       bicep.setPower(stop);
       finger.setPower(stop);
   }

 /*
 public void find_angle(double angle_I_want) {

     boolean finding_angle = true;

     //Speed stats
     double drive_speed = 1.0;
     double top_speed = 1.0;
     double bottom_speed = 0.2;

     while (finding_angle == true) {

         //finds angles
         angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

         //The bigger this is the closer it has to be to slow down
         double multiplier = 1;
         double angle_diff;


         angles.firstAngle = xy plane
         angles.secondAngle = tilt
         angles.thirdAngle = roll


         if (Math.round(angles.firstAngle) > angle_I_want) {

             //Slows down before finding correct angle to prevent the robot from over shoting it
             angle_diff = (angles.firstAngle - angle_I_want);
             drive_speed = Math.min(Math.max((angle_diff * multiplier), top_speed), bottom_speed);

             left_front_motor.setPower(-drive_speed);
             right_front_motor.setPower(-drive_speed);
             left_back_motor.setPower(-drive_speed);
             right_back_motor.setPower(-drive_speed);

         } else if (Math.round(angles.firstAngle) < angle_I_want) {

             //Slows down before finding correct angle to prevent the robot from over shoting it
             angle_diff = (angle_I_want - angles.firstAngle);
             drive_speed = Math.min(Math.max((angle_diff * multiplier), top_speed), bottom_speed);

             left_front_motor.setPower(drive_speed);
             right_front_motor.setPower(drive_speed);
             left_back_motor.setPower(drive_speed);
             right_back_motor.setPower(drive_speed);

         }

         else {

             //Tells user the robot found the right angle
             telemetry.addData("On", "track!!!");
             telemetry.update();

             //Stops all motion
             left_front_motor.setPower(0);
             right_front_motor.setPower(0);
             left_back_motor.setPower(0);
             right_back_motor.setPower(0);

             //FOUND ANGLE :)
             finding_angle = false;

         }
     }

 }//end of find angle
 */

}//end of class



