import java.util.*;

public class Timetable {

    private final Map<DayOfWeek, DaySchedule> timetable = new EnumMap<>(DayOfWeek.class);

    public Timetable() {
        for (DayOfWeek d : DayOfWeek.values()) {
            timetable.put(d, new DaySchedule());
        }
    }

    public void addNewTrainingSession(TrainingSession trainingSession) {
        DayOfWeek day = trainingSession.getDayOfWeek();
        timetable.get(day).add(trainingSession);
    }


    public List<TrainingSession> getTrainingSessionsForDay(DayOfWeek dayOfWeek) {
        return timetable.get(dayOfWeek).getAllSorted();
    }


    public List<TrainingSession> getTrainingSessionsForDayAndTime(DayOfWeek dayOfWeek, TimeOfDay timeOfDay) {
        return timetable.get(dayOfWeek).getByTime(timeOfDay);
    }


    public List<CoachCount> getCountByCoaches() {
        Map<Coach, Integer> counts = new HashMap<>();

        for (DaySchedule ds : timetable.values()) {
            for (TrainingSession s : ds.getAllSorted()) {
                counts.merge(s.getCoach(), 1, Integer::sum);
            }
        }

        List<CoachCount> result = new ArrayList<>();
        for (Map.Entry<Coach, Integer> e : counts.entrySet()) {
            result.add(new CoachCount(e.getKey(), e.getValue()));
        }

        result.sort((a, b) -> {
            int cmp = Integer.compare(b.getCount(), a.getCount());
            if (cmp != 0) return cmp;

            cmp = a.getCoach().getSurname().compareTo(b.getCoach().getSurname());
            if (cmp != 0) return cmp;
            cmp = a.getCoach().getName().compareTo(b.getCoach().getName());
            if (cmp != 0) return cmp;
            return a.getCoach().getMiddleName().compareTo(b.getCoach().getMiddleName());
        });

        return result;
    }


    private static class DaySchedule {
        private final TreeMap<TimeOfDay, List<TrainingSession>> byTime = new TreeMap<>();
        private final List<TrainingSession> allSorted = new ArrayList<>();

        void add(TrainingSession s) {
            byTime.computeIfAbsent(s.getTimeOfDay(), t -> new ArrayList<>()).add(s);
            insertIntoSortedList(s);
        }

        List<TrainingSession> getAllSorted() {
            return Collections.unmodifiableList(allSorted);
        }

        List<TrainingSession> getByTime(TimeOfDay time) {
            List<TrainingSession> list = byTime.get(time);
            return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
        }

        private void insertIntoSortedList(TrainingSession s) {

            int lo = 0, hi = allSorted.size();
            while (lo < hi) {
                int mid = (lo + hi) / 2;
                TimeOfDay midTime = allSorted.get(mid).getTimeOfDay();
                if (midTime.compareTo(s.getTimeOfDay()) <= 0) {
                    lo = mid + 1;
                } else {
                    hi = mid;
                }
            }
            allSorted.add(lo, s);
        }
    }

    public static class CoachCount {
        private final Coach coach;
        private final int count;

        public CoachCount(Coach coach, int count) {
            this.coach = coach;
            this.count = count;
        }

        public Coach getCoach() { return coach; }
        public int getCount() { return count; }
    }
}
