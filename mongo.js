
const { MongoClient } = require("mongodb");

const uri = "mongodb+srv://srija_db_user:srijaatlas@cluster0.bhqxqzi.mongodb.net/";

async function main() {
    const client = new MongoClient(uri);

    try {
        await client.connect();

        const db = client.db("StudentDB");
        const collection = db.collection("students");

        await collection.insertOne({
            rollno: 1,
            name: "Arun",
            department: "IT",
            mark: 90
        });
        console.log("One record inserted");

        await collection.insertMany([
            { rollno: 2, name: "Priya", department: "CSE", mark: 85 },
            { rollno: 3, name: "Kumar", department: "IT", mark: 88 }
        ]);
        console.log("Many records inserted");

        console.log("All Records:");
        console.log(await collection.find().toArray());

        
        console.log("Particular Record:");
        console.log(await collection.findOne({ rollno: 1 }));

       
        await collection.updateOne(
            { rollno: 1 },
            { $set: { mark: 95 } }
        );
        console.log("Record updated");

        await collection.deleteOne({ rollno: 2 });
        console.log("Record deleted");

        
        console.log("Final Records:");
        console.log(await collection.find().toArray());

    } catch (error) {
        console.error(error);
    } finally {
        await client.close();
    }
}

main();
