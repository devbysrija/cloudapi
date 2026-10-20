package org.cloudbus.cloudsim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class BasicResourceAllocation {

    public static void main(String[] args) {

        Log.printLine("Starting Resource Allocation Simulation...");

        try {
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            boolean traceFlag = false;

            CloudSim.init(numUsers, calendar, traceFlag);

            // Create Datacenter
            Datacenter datacenter = createDatacenter("Datacenter_0");

            // Create Broker
            DatacenterBroker broker =
                    new DatacenterBroker("Broker");

            int brokerId = broker.getId();

            // Create 4 VMs
            List<Vm> vmList = new ArrayList<Vm>();

            for (int i = 0; i < 4; i++) {
                Vm vm = new Vm(
                        i,
                        brokerId,
                        1000,
                        1,
                        512,
                        1000,
                        10000,
                        "Xen",
                        new CloudletSchedulerTimeShared()
                );

                vmList.add(vm);
            }

            // Submit VMs to broker
            broker.submitVmList(vmList);

            // Create 8 Cloudlets
            List<Cloudlet> cloudletList = new ArrayList<Cloudlet>();

            long fileSize = 300;
            long outputSize = 300;
            int pesNumber = 1;

            UtilizationModel utilizationModel =
                    new UtilizationModelFull();

            for (int i = 0; i < 8; i++) {

                Cloudlet cloudlet = new Cloudlet(
                        i,
                        40000,
                        pesNumber,
                        fileSize,
                        outputSize,
                        utilizationModel,
                        utilizationModel,
                        utilizationModel
                );

                cloudlet.setUserId(brokerId);
                cloudletList.add(cloudlet);
            }

            // Submit Cloudlets
            broker.submitCloudletList(cloudletList);

            // Start simulation
            CloudSim.startSimulation();

            List<Cloudlet> resultList =
                    broker.getCloudletReceivedList();

            CloudSim.stopSimulation();

            printCloudletList(resultList);

            Log.printLine("Resource Allocation Simulation finished!");

        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Simulation terminated due to an error.");
        }
    }

    private static Datacenter createDatacenter(String name) {

        List<Host> hostList = new ArrayList<Host>();

        for (int hostId = 0; hostId < 4; hostId++) {

            List<Pe> peList = new ArrayList<Pe>();

            peList.add(
                    new Pe(
                            0,
                            new PeProvisionerSimple(1000)
                    )
            );

            Host host = new Host(
                    hostId,
                    new RamProvisionerSimple(2048),
                    new BwProvisionerSimple(10000),
                    1000000,
                    peList,
                    new VmSchedulerTimeShared(peList)
            );

            hostList.add(host);
        }

        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";

        double timeZone = 10.0;
        double costPerSec = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;

        LinkedList<Storage> storageList =
                new LinkedList<Storage>();

        DatacenterCharacteristics characteristics =
                new DatacenterCharacteristics(
                        arch,
                        os,
                        vmm,
                        hostList,
                        timeZone,
                        costPerSec,
                        costPerMem,
                        costPerStorage,
                        costPerBw
                );

        Datacenter datacenter = null;

        try {
            datacenter = new Datacenter(
                    name,
                    characteristics,
                    new VmAllocationPolicySimple(hostList),
                    storageList,
                    0
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }

    private static void printCloudletList(
            List<Cloudlet> list) {

        String indent = "    ";

        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine(
                "Cloudlet ID" + indent +
                "STATUS" + indent +
                "Data center ID" + indent +
                "VM ID" + indent +
                "Time"
        );

        DecimalFormat dft = new DecimalFormat("###.##");

        for (Cloudlet cloudlet : list) {

            Log.printLine(
                    cloudlet.getCloudletId()
                    + indent
                    + "SUCCESS"
                    + indent
                    + cloudlet.getResourceId()
                    + indent
                    + cloudlet.getVmId()
                    + indent
                    + dft.format(cloudlet.getActualCPUTime())
            );
        }
    }
}