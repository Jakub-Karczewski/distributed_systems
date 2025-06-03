package sr.ice.client;

import com.zeroc.Ice.*;
import com.zeroc.Ice.Object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Exception;
import java.util.Arrays;
import java.util.Objects;

public class IceClient {
	public static void main(String[] args) {
		int status = 0;
		Communicator communicator = null;

		try {
			// 1. Inicjalizacja ICE
			communicator = Util.initialize(args);

			// 2. Uzyskanie referencji obiektu na podstawie linii w pliku konfiguracyjnym (wówczas aplikację należy uruchomić z argumentem --Ice.config=config.client)
			//ObjectPrx base1 = communicator.propertyToProxy("Calc1.Proxy");

			// 2. Uzyskanie referencji obiektu - to samo co powyżej, ale mniej ładnie
			ObjectPrx base = communicator.stringToProxy("calc/calc11:tcp -h 127.0.0.2 -p 10000 -z : udp -h 127.0.0.2 -p 10000 -z"); //opcja -z włącza możliwość kompresji wiadomości

			if (base == null) throw new Error("Invalid proxy");

			String line = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			double result;

			do {
				try {
					System.out.print("==> ");
					line = in.readLine();
					switch (line) {
						case "divide":
							result = calcOperation(base, "divide", 3.0d, 0.4d);
							System.out.println("RESULT = " + result);
							break;
						case "subtract":
							result = calcOperation(base, "subtract", 7.0d, 4.0d);
							System.out.println("RESULT = " + result);
							break;
						case "map":
							int[] nums = {1, 2, 5, 8};
							double[] numsResult = mapOperation(base, nums);
							System.out.println("RESULT = " + Arrays.toString(numsResult));
							break;
						case "desc":
							System.out.println("RESULT = " + getDescriptionOperation(base));
							break;
						default:
							System.out.println("???");
					}
				} catch (IOException | TwowayOnlyException | UnknownException ex) {
					ex.printStackTrace(System.err);
				}
			}
			while (!Objects.equals(line, "x"));


		} catch (LocalException e) {
			e.printStackTrace();
			status = 1;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			status = 1;
		}
		if (communicator != null) {
			try {
				communicator.destroy();
			} catch (Exception e) {
				System.err.println(e.getMessage());
				status = 1;
			}
		}
		System.exit(status);
	}

	private static double calcOperation(ObjectPrx base, String operationName, double a, double b){
		System.out.println("Operation '" + operationName + "' ...");

		OutputStream outStream = new OutputStream(base.ice_getCommunicator());
		outStream.startEncapsulation();
		outStream.writeDouble(a);
		outStream.writeDouble(b);
		outStream.endEncapsulation();

		try {
			Object.Ice_invokeResult result = base.ice_invoke(operationName, OperationMode.Normal, outStream.finished());
			InputStream inStream = new InputStream(base.ice_getCommunicator(), result.outParams);
			inStream.startEncapsulation();
			double returnValue = inStream.readDouble();
			inStream.endEncapsulation();
			return returnValue;
		} catch (Exception e) {
			throw new RuntimeException("Server returned an error during " + operationName + " operation: " + e);
		}
	}

	private static double[] mapOperation(ObjectPrx base, int[] nums) {
		System.out.println("Operation 'map' ...");

		OutputStream outStream = new OutputStream(base.ice_getCommunicator());
		outStream.startEncapsulation();
		outStream.writeIntSeq(nums);
		outStream.endEncapsulation();

		try {
			Object.Ice_invokeResult result = base.ice_invoke("map", OperationMode.Normal, outStream.finished());
			InputStream inStream = new InputStream(base.ice_getCommunicator(), result.outParams);
			inStream.startEncapsulation();
			double[] returnValue = inStream.readDoubleSeq();
			inStream.endEncapsulation();
			return returnValue;
		} catch (Exception e) {
			throw new RuntimeException("Server returned an error for map operation: " + e);
		}
	}

	private static String getDescriptionOperation(ObjectPrx base) {
		System.out.println("Operation 'getdescription' ...");

		OutputStream outStream = new OutputStream(base.ice_getCommunicator());
		outStream.startEncapsulation();
		outStream.endEncapsulation();

		try {
			Object.Ice_invokeResult result = base.ice_invoke("getdescription", OperationMode.Idempotent, outStream.finished());
			InputStream inStream = new InputStream(base.ice_getCommunicator(), result.outParams);
			inStream.startEncapsulation();
			String returnValue = inStream.readString();
			inStream.endEncapsulation();
			return returnValue;
		} catch (Exception e) {
			throw new RuntimeException("Server returned an error for map operation: " + e);
		}
	}
}