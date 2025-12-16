import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TimetableTest {

    @Test
    void testGetTrainingSessionsForDaySingleSession() {
        Timetable timetable = new Timetable();

        Group group = new Group("Акробатика для детей", Age.CHILD, 60);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession singleTrainingSession = new TrainingSession(
                group, coach, DayOfWeek.MONDAY, new TimeOfDay(13, 0)
        );

        timetable.addNewTrainingSession(singleTrainingSession);

        List<TrainingSession> monday = timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY);
        assertEquals(1, monday.size());
        assertSame(singleTrainingSession, monday.get(0));

        List<TrainingSession> tuesday = timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY);
        assertTrue(tuesday.isEmpty());
    }

    @Test
    void testGetTrainingSessionsForDayMultipleSessions() {
        Timetable timetable = new Timetable();

        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");

        Group groupAdult = new Group("Акробатика для взрослых", Age.ADULT, 90);
        TrainingSession thursdayAdultTrainingSession = new TrainingSession(
                groupAdult, coach, DayOfWeek.THURSDAY, new TimeOfDay(20, 0)
        );
        timetable.addNewTrainingSession(thursdayAdultTrainingSession);

        Group groupChild = new Group("Акробатика для детей", Age.CHILD, 60);
        TrainingSession mondayChildTrainingSession = new TrainingSession(
                groupChild, coach, DayOfWeek.MONDAY, new TimeOfDay(13, 0)
        );
        TrainingSession thursdayChildTrainingSession = new TrainingSession(
                groupChild, coach, DayOfWeek.THURSDAY, new TimeOfDay(13, 0)
        );
        TrainingSession saturdayChildTrainingSession = new TrainingSession(
                groupChild, coach, DayOfWeek.SATURDAY, new TimeOfDay(10, 0)
        );

        timetable.addNewTrainingSession(mondayChildTrainingSession);
        timetable.addNewTrainingSession(thursdayChildTrainingSession);
        timetable.addNewTrainingSession(saturdayChildTrainingSession);

        List<TrainingSession> monday = timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY);
        assertEquals(1, monday.size());
        assertSame(mondayChildTrainingSession, monday.get(0));

        List<TrainingSession> thursday = timetable.getTrainingSessionsForDay(DayOfWeek.THURSDAY);
        assertEquals(2, thursday.size());
        assertSame(thursdayChildTrainingSession, thursday.get(0));
        assertSame(thursdayAdultTrainingSession, thursday.get(1));

        assertTrue(timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY).isEmpty());
    }

    @Test
    void testGetTrainingSessionsForDayAndTime() {
        Timetable timetable = new Timetable();

        Group group = new Group("Акробатика для детей", Age.CHILD, 60);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession singleTrainingSession = new TrainingSession(
                group, coach, DayOfWeek.MONDAY, new TimeOfDay(13, 0)
        );

        timetable.addNewTrainingSession(singleTrainingSession);

        List<TrainingSession> at13 = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(13, 0)
        );
        assertEquals(1, at13.size());
        assertSame(singleTrainingSession, at13.get(0));

        List<TrainingSession> at14 = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(14, 0)
        );
        assertTrue(at14.isEmpty());
    }


    @Test
    void testMultipleSessionsSameDaySameTime() {
        Timetable timetable = new Timetable();

        Coach coach1 = new Coach("Иванов", "Иван", "Иванович");
        Coach coach2 = new Coach("Петров", "Пётр", "Петрович");
        Group group = new Group("Акробатика", Age.CHILD, 60);

        TrainingSession s1 = new TrainingSession(group, coach1, DayOfWeek.MONDAY, new TimeOfDay(10, 0));
        TrainingSession s2 = new TrainingSession(group, coach2, DayOfWeek.MONDAY, new TimeOfDay(10, 0));

        timetable.addNewTrainingSession(s1);
        timetable.addNewTrainingSession(s2);

        assertEquals(2, timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, new TimeOfDay(10, 0)).size());
        assertEquals(2, timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY).size());
    }


    @Test
    void testSortingKeepsAfterOutOfOrderInsert() {
        Timetable timetable = new Timetable();

        Coach coach = new Coach("Сидоров", "Сидор", "Сидорович");
        Group group = new Group("ОФП", Age.ADULT, 45);

        TrainingSession s14 = new TrainingSession(group, coach, DayOfWeek.FRIDAY, new TimeOfDay(14, 0));
        TrainingSession s10 = new TrainingSession(group, coach, DayOfWeek.FRIDAY, new TimeOfDay(10, 0));
        TrainingSession s12 = new TrainingSession(group, coach, DayOfWeek.FRIDAY, new TimeOfDay(12, 0));

        timetable.addNewTrainingSession(s14);
        timetable.addNewTrainingSession(s10);
        timetable.addNewTrainingSession(s12);

        List<TrainingSession> friday = timetable.getTrainingSessionsForDay(DayOfWeek.FRIDAY);
        assertSame(s10, friday.get(0));
        assertSame(s12, friday.get(1));
        assertSame(s14, friday.get(2));
    }


    @Test
    void testGetCountByCoachesSortedDesc() {
        Timetable timetable = new Timetable();

        Coach c1 = new Coach("А", "А", "А");
        Coach c2 = new Coach("Б", "Б", "Б");
        Group g = new Group("Группа", Age.CHILD, 60);

        timetable.addNewTrainingSession(new TrainingSession(g, c1, DayOfWeek.MONDAY, new TimeOfDay(10, 0)));
        timetable.addNewTrainingSession(new TrainingSession(g, c1, DayOfWeek.TUESDAY, new TimeOfDay(10, 0)));
        timetable.addNewTrainingSession(new TrainingSession(g, c2, DayOfWeek.WEDNESDAY, new TimeOfDay(10, 0)));

        List<Timetable.CoachCount> counts = timetable.getCountByCoaches();
        assertEquals(2, counts.size());

        assertEquals(c1, counts.get(0).getCoach());
        assertEquals(2, counts.get(0).getCount());

        assertEquals(c2, counts.get(1).getCoach());
        assertEquals(1, counts.get(1).getCount());
    }


    @Test
    void testGetCountByCoachesEmpty() {
        Timetable timetable = new Timetable();
        assertTrue(timetable.getCountByCoaches().isEmpty());
    }
}
