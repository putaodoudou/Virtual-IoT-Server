import mraa.Aio;
import mraa.Gpio;
import mraa.Pwm;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SmartDoorLock {

  static {
    try {
      System.loadLibrary("mraajava");
    } catch (UnsatisfiedLinkError e) {
      System.err.println(
          "Native code library failed to load. See the chapter on Dynamic Linking Problems in the SWIG Java documentation for help.\n" +
          e);
      System.exit(1);
    }
  }
  public static void main(String[] args){
    //instantiate the sensors/motor
    Gpio button = new Gpio(3);
    Aio light = new Aio(3);
    Pwm servo = new Pwm(6);

    //setting default password
    double password = 1111;
    double enteredPassword;

    //test to see if it works

    lock(servo);
    enteredPassword = changePassword(button);

    if(password == enteredPassword) {
      System.out.println("Correct password.");
      unlock(servo);
    }
    else {
      System.out.println("Incorrect password.");
    }

  }

  public static void unlock(Pwm door) {
    door.enable(true);
    door.period_ms(20);
    door.pulsewidth_us(500);
    try {
      TimeUnit.SECONDS.sleep(1);
    }catch (InterruptedException e) {
    }
    door.enable(false);
  }

  public static void lock(Pwm door) {
    door.enable(true);
    door.period_ms(20);
    door.pulsewidth_us(2500);
    try {
      TimeUnit.SECONDS.sleep(1);
    }catch (InterruptedException e) {
    }
    door.enable(false);
  }

  public static double changePassword(Gpio doorButton) {

    ArrayList<Integer> generatedPassword = new ArrayList<Integer>();
    int passLength = 0;
    int value = 0;
    double pass = 0;
    int shouldContinue = 0;

    BufferedReader length = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("How long is the password?");

    try {
      passLength = Integer.parseInt(length.readLine());
    } catch (Exception e)  {}

    for (int i = 0; i < passLength; i++) {
      BufferedReader cont = new BufferedReader(new InputStreamReader(System.in));
      System.out.println("Type 1 to continue recording the password: ");

      try {
      shouldContinue = Integer.parseInt(cont.readLine());
      } catch (Exception e) {}

      if (shouldContinue == 1) {
        value = doorButton.read();
        generatedPassword.add(value);
      }
      else {
        break;
      }

    }

      for(int i = 0; i < passLength; i++){
      pass = pass + (Math.pow(10,(passLength-i-1)) * generatedPassword.get(i));
      }

      /*for(int d:generatedPassword) {
              System.out.println(d);
          }*/

    return pass;
  }

}
