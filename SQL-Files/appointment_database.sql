USE customer_database_schema;
DROP TABLE IF EXISTS Appointment;
create TABLE Appointment(
Appointment_Id int AUTO_INCREMENT,
Appointment_Time varChar(32),
Customer_id int,
Service_id int,
 primary key(Appointment_Id)
);
insert into appointment(Appointment_Id,Appointment_Time,Customer_id,Service_id)
values(1,'10:00:00',2,3),
(2,'13:00:00',4,5),
(3,'15:00:00',6,7),
(4,'18:00:00',8,9);
select * from Appointment;