import json
import mysql.connector 

mydb = mysql.connector.connect(
    host = "localhost",
    user = "root",
    passwd = "12345",
)

mydb.reset_session()
my_cursor = mydb.cursor()
my_cursor.execute("USE aubsocial")

with open('set1.json', 'r') as file:
    data = json.load(file)
courses = data['data']

for course in courses:
    course_name = course['subject'] + " " + course["courseNumber"]
    section = course['sequenceNumber']
    professor = [faculty['displayName'] for faculty in course['faculty'] if faculty["primaryIndicator"]]
    location = course['meetingsFaculty'][0]['meetingTime']['buildingDescription']
    room = course['meetingsFaculty'][0]['meetingTime']['room']
    meeting_times = {
        'days': [key.capitalize() for key, value in course['meetingsFaculty'][0]['meetingTime'].items() if value and key in ['monday', 'tuesday', 'wednesday', 'thursday', 'friday']],
        'start_time': course['meetingsFaculty'][0]['meetingTime']['beginTime'],
        'end_time': course['meetingsFaculty'][0]['meetingTime']['endTime']
    }
    print(f"Course: {course_name} (Section: {section})")
    print(f"Professor(s): {', '.join(professor)}")
    print(f"Location: {location}, Room: {room}")
    print("Meeting Times:")
    print("-----------------------------")
    if( course_name == None or section == None or location == None or section == None or room == None or meeting_times == None or professor == [] ): continue
    else:
        my_cursor.execute(f"""
            INSERT INTO course(name, section, professor, location, room, meeting_time)
            VALUES ("{course_name}","{section}","{professor[0]}","{location}", "{room}", "{meeting_times}");
        """)
        mydb.commit()


