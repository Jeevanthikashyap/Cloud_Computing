
package FCFS;


import org.cloudbus.cloudsim.*;

import org.cloudbus.cloudsim.core.CloudSim;
import FCFS.Constants;
import FCFS.DatacenterCreator;
import FCFS.GenerateMatrices;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class FCFS_Scheduler {

    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmList;
    private static Datacenter[] datacenter;
    private static double[][] commMatrix;
    private static double[][] execMatrix;

    private static List<Vm> createVM(int userId, int vms) {
        //Creates a container to store VMs. This list is passed to the broker later
        LinkedList<Vm> list = new LinkedList<Vm>();

        //VM Parameters
        long size = 10000; //image size (MB)
        int ram = 512; //vm memory (MB)
        int mips = 250;
      
        long bw = 1000;
        int pesNumber = 1; //number of cpus
        String vmm = "Xen"; //VMM name

        //create VMs
        Vm[] vm = new Vm[vms];

        for (int i = 0; i < vms; i++) {
        	
        	
            vm[i] = new Vm(i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
            Log.printLine(vm[i].getSize());
        }

        return list;
    }

    private static List<Cloudlet> createCloudlet(int userId, int cloudlets) {
        // Creates a container to store Cloudlets
        LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

        //cloudlet parameters
        long fileSize = 3000;
        long outputSize = 300;
        long length = 100000;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        Cloudlet[] cloudlet = new Cloudlet[cloudlets];

        for (int i = 0; i < cloudlets; i++) {
            //int dcId = (int) (Math.random() * Constants.NO_OF_DATA_CENTERS);
            //long length = (long) (1e3 * (commMatrix[i][dcId] + execMatrix[i][dcId]));
            if(i==0)
                length =length +0;
                 else if(i==1)
                 length =length -30000;
                 else if(i==2)
                 length =length -65000;
                 else if(i==3)
                     length =length -4000;
                 else if(i==4)
                     length =length +2000;
                 else if(i==5)
                     length =length +7000;
                 else if(i==6)
                     length =length +80000;
                     else if(i==7)
                     length =length+ 10000;
                     else if(i==8)
                         length =length-85000;
                     else if(i==9)
                         length =length -14000;
                     else if(i==10)
                         length =length+1000;
                     else if(i==11)
                         length =length+2000;
                     else if(i==12)
                         length =length +16000;
                     else if(i==13)
                         length =length +5000;
                     else if(i==14)
                         length =length +55000;
                     
            cloudlet[i] = new Cloudlet(i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            // setting the owner of these Cloudlets
            cloudlet[i].setUserId(userId);
            //cloudlet[i].setVmId(dcId + 2);
            list.add(cloudlet[i]);
        }
        return list;
    }

    public static void main(String[] args) {
        Log.printLine("Starting FCFS Scheduler...");

        new GenerateMatrices();
        execMatrix = GenerateMatrices.getExecMatrix();
        commMatrix = GenerateMatrices.getCommMatrix();

        try {
            int num_user = 1;   // number of grid users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;  // mean trace events

            CloudSim.init(num_user, calendar, trace_flag);

//            // Second step: Create Datacenters
//            datacenter = new Datacenter[Constants.NO_OF_DATA_CENTERS];
//            for (int i = 0; i < Constants.NO_OF_DATA_CENTERS; i++) {
//                datacenter[i] = DatacenterCreator.createDatacenter("Datacenter_" + i);
//            }
            Datacenter datacenter0 = DatacenterCreator.createDatacenter("Datacenter_0");
            //Third step: Create Broker
            FCFSDatacenterBroker broker = createBroker("Broker_0");
            int brokerId = broker.getId();

//            //Fourth step: Create VMs and Cloudlets and send them to broker
//            vmList = createVM(brokerId, Constants.NO_OF_DATA_CENTERS);
//            cloudletList = createCloudlet(brokerId, Constants.NO_OF_TASKS, 0);

            vmList = createVM(brokerId,6); //creating 20 vms
			cloudletList = createCloudlet(brokerId,15);
           
            broker.submitVmList(vmList);
            broker.submitCloudletList(cloudletList);

            // Fifth step: Starts the simulation
            CloudSim.startSimulation();

            // Final step: Print results when simulation is over
            List<Cloudlet> newList = broker.getCloudletReceivedList();
            //newList.addAll(globalBroker.getBroker().getCloudletReceivedList());

            CloudSim.stopSimulation();

            printCloudletList(newList);

            Log.printLine(FCFS_Scheduler.class.getName() + " finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }

    private static FCFSDatacenterBroker createBroker(String name) throws Exception {
        return new FCFSDatacenterBroker(name);
    }

    /**
     * Prints the Cloudlet objects
     *
     * @param list list of Cloudlets
     */
    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" +indent + "Length" +
                indent + "Data center ID" +
                indent + "VM ID" +
                indent + indent + "Time" +
                indent + "Start Time" +
                indent + "Finish Time" +
                indent + "Waiting Time" +
                indent + "Response Time" +
                indent + " Execution Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        dft.setMinimumIntegerDigits(2);
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + dft.format(cloudlet.getCloudletId()) + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                Log.printLine(
                		indent + indent +  dft.format(cloudlet.getCloudletTotalLength()) 
                		+indent + indent + dft.format(cloudlet.getResourceId()) +
                        indent + indent + indent + dft.format(cloudlet.getVmId()) +
                        indent + indent + dft.format(cloudlet.getActualCPUTime()) +
                        indent + indent + dft.format(cloudlet.getExecStartTime()) +
                        indent + indent + indent + dft.format(cloudlet.getFinishTime())+
                        indent + indent + indent +dft.format(cloudlet.getWaitingTime())+
                        indent + indent + indent  +dft.format(cloudlet.getFinishTime() - cloudlet.getSubmissionTime())+
                        indent + indent + indent +dft.format(cloudlet.getFinishTime() - cloudlet.getExecStartTime()));
            }
        }
        double makespan = calcMakespan(list);
        Log.printLine("Makespan using FCFS: " + makespan);
    }

    private static double calcMakespan(List<Cloudlet> list) {
        double makespan = 0;
        double[] dcWorkingTime = new double[Constants.NO_OF_DATA_CENTERS];

        for (int i = 0; i < list.size(); i++) {
            int dcId = list.get(i).getVmId() % Constants.NO_OF_DATA_CENTERS;
            if (dcWorkingTime[dcId] != 0) --dcWorkingTime[dcId];
            dcWorkingTime[dcId] += execMatrix[i][dcId] + commMatrix[i][dcId];
            makespan = Math.max(makespan, dcWorkingTime[dcId]);
        }
        return makespan;
    }
}
