package de.awi.tournamentsimulator.euro2024;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TournamentSimulator {
    private static final Logger log = LogManager.getLogger(TournamentSimulator.class.getName());
    private final Set<String> groups = new HashSet<>();
    private final Random random;
    private final String dateString;
    private final List<Team> teams;
    private Map<KnockoutStageRoundName, Map<KnockoutStageMatch, Integer>> knockoutStageMatches;
    private Map<GroupStageResult, Integer> groupStageResult;

    public TournamentSimulator(File input) {
        this.random = new Random(0);
        this.knockoutStageMatches = new HashMap<>();
        this.groupStageResult = new HashMap<>();
        int idxStart = input.getName().indexOf("_");
        this.dateString = input.getName().substring(idxStart+1, idxStart + 9);

        final InputReader reader = new InputReader(input);
        this.teams = reader.getTeams();
        teams.forEach(team -> groups.add(team.group));
        if (log.isDebugEnabled()) {
            log.debug("groups: " + groups);
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            log.info("usage: {} <inputFile.txt>", TournamentSimulator.class.getName());
            System.exit(-1);
        }

        final long start = System.currentTimeMillis();
        log.info("==================== start ====================");

        TournamentSimulator simulator = new TournamentSimulator(new File(args[0].trim()));
        simulator.run();
        simulator.writeResult();

        final long end = System.currentTimeMillis();
        log.info("processing time in cpu-millis: {}", (double) (end - start));
        log.info("==================== end ====================");
    }

    private void run() {
        for (int i = 0; i < Util.N_ITERATIONS; i++) {
            for (int j = 0; j < Util.N_SIMULATIONS; j++) {
                simulate();
            }

            if (checkTeamAbility(i)) {
                break;
            }
        }
    }

    private boolean checkTeamAbility(int iter) {
        AbilityAdaptor adaptor = new AbilityAdaptor(teams);
        log.info("iteration = {}, relative error = {}", iter + 1, String.format(Locale.ENGLISH, "%4f", adaptor.getRelativeError()));

        if (adaptor.getRelativeError() < Util.TARGET_RELATIVE_ERROR) {
            return true;
        }

        // reset teams
        this.teams.forEach(team -> {
            team.groupStage.standing = null;
            team.groupStage.points = 0;
            team.knockoutStage.countWinner = 0;
            team.knockoutStage.rounds.forEach(round -> round.count = 0);
        });

        // result results
        this.knockoutStageMatches = new HashMap<>();
        this.groupStageResult = new HashMap<>();

        return false;
    }

    private void simulate() {
        simulateGroupStage();
        simulateKnockoutStage();
    }

    private void simulateKnockoutStage() {
        // RoundOf16
        Team team1GroupA = filterTeamByGroupStanding(GroupName.A.name(), 1);
        Team team2GroupA = filterTeamByGroupStanding(GroupName.A.name(), 2);
        Team team1GroupB = filterTeamByGroupStanding(GroupName.B.name(), 1);
        Team team2GroupB = filterTeamByGroupStanding(GroupName.B.name(), 2);
        Team team1GroupC = filterTeamByGroupStanding(GroupName.C.name(), 1);
        Team team2GroupC = filterTeamByGroupStanding(GroupName.C.name(), 2);
        Team team1GroupD = filterTeamByGroupStanding(GroupName.D.name(), 1);
        Team team2GroupD = filterTeamByGroupStanding(GroupName.D.name(), 2);
        Team team1GroupE = filterTeamByGroupStanding(GroupName.E.name(), 1);
        Team team2GroupE = filterTeamByGroupStanding(GroupName.E.name(), 2);
        Team team1GroupF = filterTeamByGroupStanding(GroupName.F.name(), 1);
        Team team2GroupF = filterTeamByGroupStanding(GroupName.F.name(), 2);
        Team team3GroupA = filterTeamByGroupStanding(GroupName.A.name(), 3);
        Team team3GroupB = filterTeamByGroupStanding(GroupName.B.name(), 3);
        Team team3GroupC = filterTeamByGroupStanding(GroupName.C.name(), 3);
        Team team3GroupD = filterTeamByGroupStanding(GroupName.D.name(), 3);
        Team team3GroupE = filterTeamByGroupStanding(GroupName.E.name(), 3);
        Team team3GroupF = filterTeamByGroupStanding(GroupName.F.name(), 3);

        Team team3GroupDEF = getTeamByProbability(Arrays.asList(team3GroupD, team3GroupE, team3GroupF), null);
        Team team3GroupADEF = getTeamByProbability(Arrays.asList(team3GroupA, team3GroupD, team3GroupE, team3GroupF), Collections.singletonList(team3GroupDEF));
        Team team3GroupABC = getTeamByProbability(Arrays.asList(team3GroupA, team3GroupB, team3GroupC), Arrays.asList(team3GroupDEF, team3GroupADEF));
        Team team3GroupABCD = getTeamByProbability(Arrays.asList(team3GroupA, team3GroupB, team3GroupC, team3GroupD), Arrays.asList(team3GroupDEF, team3GroupADEF, team3GroupABC));

        updateKnockoutStageRound(team1GroupA, KnockoutStageRoundName.RoundOf16);
        updateKnockoutStageRound(team2GroupA, KnockoutStageRoundName.RoundOf16);
        updateKnockoutStageRound(team1GroupB, KnockoutStageRoundName.RoundOf16);
        updateKnockoutStageRound(team2GroupB, KnockoutStageRoundName.RoundOf16);
        updateKnockoutStageRound(team1GroupC, KnockoutStageRoundName.RoundOf16);
        updateKnockoutStageRound(team2GroupC, KnockoutStageRoundName.RoundOf16);
        updateKnockoutStageRound(team1GroupD, KnockoutStageRoundName.RoundOf16);
        updateKnockoutStageRound(team2GroupD, KnockoutStageRoundName.RoundOf16);
        updateKnockoutStageRound(team1GroupE, KnockoutStageRoundName.RoundOf16);
        updateKnockoutStageRound(team2GroupE, KnockoutStageRoundName.RoundOf16);
        updateKnockoutStageRound(team1GroupF, KnockoutStageRoundName.RoundOf16);
        updateKnockoutStageRound(team2GroupF, KnockoutStageRoundName.RoundOf16);
        updateKnockoutStageRound(team3GroupDEF, KnockoutStageRoundName.RoundOf16);
        updateKnockoutStageRound(team3GroupADEF, KnockoutStageRoundName.RoundOf16);
        updateKnockoutStageRound(team3GroupABC, KnockoutStageRoundName.RoundOf16);
        updateKnockoutStageRound(team3GroupABCD, KnockoutStageRoundName.RoundOf16);

        Team teamQuarter1 = calcKnockoutStageWinner(team2GroupA, team2GroupB, KnockoutStageRoundName.RoundOf16);
        Team teamQuarter2 = calcKnockoutStageWinner(team1GroupA, team2GroupC, KnockoutStageRoundName.RoundOf16);
        Team teamQuarter3 = calcKnockoutStageWinner(team1GroupC, team3GroupDEF, KnockoutStageRoundName.RoundOf16);
        Team teamQuarter4 = calcKnockoutStageWinner(team1GroupB, team3GroupADEF, KnockoutStageRoundName.RoundOf16);
        Team teamQuarter5 = calcKnockoutStageWinner(team2GroupD, team2GroupE, KnockoutStageRoundName.RoundOf16);
        Team teamQuarter6 = calcKnockoutStageWinner(team1GroupF, team3GroupABC, KnockoutStageRoundName.RoundOf16);
        Team teamQuarter7 = calcKnockoutStageWinner(team1GroupD, team2GroupF, KnockoutStageRoundName.RoundOf16);
        Team teamQuarter8 = calcKnockoutStageWinner(team1GroupE, team3GroupABCD, KnockoutStageRoundName.RoundOf16);

        // QuarterFinals
        updateKnockoutStageRound(teamQuarter1, KnockoutStageRoundName.QuarterFinals);
        updateKnockoutStageRound(teamQuarter2, KnockoutStageRoundName.QuarterFinals);
        updateKnockoutStageRound(teamQuarter3, KnockoutStageRoundName.QuarterFinals);
        updateKnockoutStageRound(teamQuarter4, KnockoutStageRoundName.QuarterFinals);
        updateKnockoutStageRound(teamQuarter5, KnockoutStageRoundName.QuarterFinals);
        updateKnockoutStageRound(teamQuarter6, KnockoutStageRoundName.QuarterFinals);
        updateKnockoutStageRound(teamQuarter7, KnockoutStageRoundName.QuarterFinals);
        updateKnockoutStageRound(teamQuarter8, KnockoutStageRoundName.QuarterFinals);

        Team teamSemi1 = calcKnockoutStageWinner(teamQuarter6, teamQuarter5, KnockoutStageRoundName.QuarterFinals);
        Team teamSemi2 = calcKnockoutStageWinner(teamQuarter4, teamQuarter2, KnockoutStageRoundName.QuarterFinals);
        Team teamSemi3 = calcKnockoutStageWinner(teamQuarter3, teamQuarter1, KnockoutStageRoundName.QuarterFinals);
        Team teamSemi4 = calcKnockoutStageWinner(teamQuarter8, teamQuarter7, KnockoutStageRoundName.QuarterFinals);

        // SemiFinals
        updateKnockoutStageRound(teamSemi1, KnockoutStageRoundName.SemiFinals);
        updateKnockoutStageRound(teamSemi2, KnockoutStageRoundName.SemiFinals);
        updateKnockoutStageRound(teamSemi3, KnockoutStageRoundName.SemiFinals);
        updateKnockoutStageRound(teamSemi4, KnockoutStageRoundName.SemiFinals);

        Team teamFinal1 = calcKnockoutStageWinner(teamSemi1, teamSemi2, KnockoutStageRoundName.SemiFinals);
        Team teamFinal2 = calcKnockoutStageWinner(teamSemi3, teamSemi4, KnockoutStageRoundName.SemiFinals);

        // Final
        updateKnockoutStageRound(teamFinal1, KnockoutStageRoundName.Final);
        updateKnockoutStageRound(teamFinal2, KnockoutStageRoundName.Final);

        Team teamWinner = calcKnockoutStageWinner(teamFinal1, teamFinal2, KnockoutStageRoundName.Final);
        teamWinner.knockoutStage.countWinner++;
    }

    private Team getTeamByProbability(final List<Team> teams, List<Team> teamsToExclude) {
        List<Team> teamsForSelection = new ArrayList<>(teams);

        if (teamsToExclude != null) {
            teamsForSelection = teamsForSelection
                    .stream()
                    .filter(team -> !teamsToExclude.contains(team))
                    .collect(Collectors.toList());
        }

        // Step 1: filter teams with max points
          int maxPoints = teamsForSelection.stream()
                .mapToInt(team -> team.groupStage.points)
                .max()
                .orElse(0);

          List<Team> teamsWithMaxPoints = teamsForSelection.stream()
                .filter(team -> team.groupStage.points == maxPoints)
                .collect(Collectors.toList());

          // Step 2: filter teams with the maximum probability
        return teamsWithMaxPoints
                .stream()
                .max(Comparator.comparing(Team::getProbability))
                .orElseThrow(NoSuchElementException::new);
    }

    private Team calcKnockoutStageWinner(final Team team1, final Team team2, KnockoutStageRoundName stageRoundName) {
        Map<KnockoutStageMatch, Integer> matches = knockoutStageMatches.get(stageRoundName);
        if (matches == null) {
            matches = new HashMap<>();
        }

        KnockoutStageMatch match = new KnockoutStageMatch(team1, team2);
        matches.merge(match, 1, Integer::sum);
        knockoutStageMatches.put(stageRoundName, matches);

        return Util.calcWinner(team1, team2, random);
    }

    private void updateKnockoutStageRound(final Team team, KnockoutStageRoundName stageRoundName) {
        team.knockoutStage.rounds.stream().filter(round -> round.name.equals(stageRoundName.name())).collect(Collectors.toList()).get(0).count++;
    }

    private Team filterTeamByGroupStanding(String group, Integer standing) {
        return teams.stream().filter(team -> team.group.equals(group) && team.groupStage.standing.equals(standing)).collect(Collectors.toList()).get(0);
    }

    private void simulateGroupStage() {
        groups.forEach(group -> {
            List<Team> teamsInGroup = teams.stream().filter(team -> team.group.equals(group)).collect(Collectors.toList());
            new GroupStageCalculator(teamsInGroup, random);
            updateGroupStageResult(teamsInGroup);

            // update teams
            teams.forEach(team -> teamsInGroup.forEach(teamInGroup -> {
                if (team.equals(teamInGroup)) {
                    team.groupStage = teamInGroup.groupStage;
                }
            }));
        });
    }

    private void updateGroupStageResult(final List<Team> teamsInGroup) {
        GroupStageResult result = new GroupStageResult(
                teamsInGroup.stream().filter(t -> t.groupStage.standing == 1).collect(Collectors.toList()).get(0),
                teamsInGroup.stream().filter(t -> t.groupStage.standing == 2).collect(Collectors.toList()).get(0),
                teamsInGroup.stream().filter(t -> t.groupStage.standing == 3).collect(Collectors.toList()).get(0),
                teamsInGroup.stream().filter(t -> t.groupStage.standing == 4).collect(Collectors.toList()).get(0));
        groupStageResult.merge(result, 1, Integer::sum);
    }

    private void writeResult() {
        writeTeamResult();
        writeGroupStageResult();
        writeKnockoutStageResult();
    }

    private void writeKnockoutStageResult() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("knockoutStageResult" + dateString + ".csv"))) {
            final String header =
                    "round" + Util.CSV_SEPARATOR +
                            "team1" + Util.CSV_SEPARATOR +
                            "team2" + Util.CSV_SEPARATOR +
                            "n" + Util.CSV_SEPARATOR +
                            "p" + "\n";
            bw.write(header);

            knockoutStageMatches.forEach((key, value) -> value.forEach((key1, value1) -> {
                String line =
                        key.name() + Util.CSV_SEPARATOR +
                                key1.team1.name + Util.CSV_SEPARATOR +
                                key1.team2.name + Util.CSV_SEPARATOR +
                                value1 + Util.CSV_SEPARATOR +
                                String.format(Locale.ENGLISH, "%4f", value1.doubleValue() / (double) Util.N_SIMULATIONS) + "\n";
                try {
                    bw.write(line);
                } catch (IOException e) {
                    log.error("could not write line {}", line);
                    e.printStackTrace();
                }
            }));
        } catch (final Exception e) {
            log.error(e.getMessage());
        }
    }

    private void writeGroupStageResult() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("groupStageResult" + dateString + ".csv"))) {
            final String header =
                    "group" + Util.CSV_SEPARATOR +
                            "first" + Util.CSV_SEPARATOR +
                            "second" + Util.CSV_SEPARATOR +
                            "third" + Util.CSV_SEPARATOR +
                            "forth" + Util.CSV_SEPARATOR +
                            "n" + Util.CSV_SEPARATOR +
                            "p" + "\n";
            bw.write(header);

            groups.forEach(group -> {
                List<Map.Entry<GroupStageResult, Integer>> entries = groupStageResult.entrySet().stream().filter(result -> result.getKey().first.group.equals(group)).collect(Collectors.toList());
                entries.forEach(entry -> {
                    GroupStageResult key = entry.getKey();
                    String line =
                            group + Util.CSV_SEPARATOR +
                                    key.first.name + Util.CSV_SEPARATOR +
                                    key.second.name + Util.CSV_SEPARATOR +
                                    key.third.name + Util.CSV_SEPARATOR +
                                    key.fourth.name + Util.CSV_SEPARATOR +
                                    entry.getValue() + Util.CSV_SEPARATOR +
                                    String.format(Locale.ENGLISH, "%4f", entry.getValue().doubleValue() / (double) Util.N_SIMULATIONS) + "\n";
                    try {
                        bw.write(line);
                    } catch (IOException e) {
                        log.error("could not write line {}", line);
                        e.printStackTrace();
                    }
                });
            });
        } catch (final Exception e) {
            log.error(e.getMessage());
        }
    }

    private void writeTeamResult() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("teamResults" + dateString + ".csv"))) {
            final String header =
                    "code" + Util.CSV_SEPARATOR +
                            "group" + Util.CSV_SEPARATOR +
                            "pWinning" + Util.CSV_SEPARATOR +
                            "pFinalist" + Util.CSV_SEPARATOR +
                            "pSemiFinalist" + Util.CSV_SEPARATOR +
                            "pQuarterFinalist" + Util.CSV_SEPARATOR +
                            "pRoundOfSixteen" + Util.CSV_SEPARATOR +
                            "ability" + "\n";
            bw.write(header);

            teams.forEach(team -> {
                String line =
                        team.name + Util.CSV_SEPARATOR +
                                team.group + Util.CSV_SEPARATOR +
                                String.format(Locale.ENGLISH, "%4f", team.knockoutStage.countWinner.doubleValue() / (double) Util.N_SIMULATIONS) + Util.CSV_SEPARATOR +
                                String.format(Locale.ENGLISH, "%4f", team.knockoutStage.rounds.stream().filter(round -> round.name.equals(KnockoutStageRoundName.Final.name())).collect(Collectors.toList()).get(0).count.doubleValue() / (double) Util.N_SIMULATIONS) + Util.CSV_SEPARATOR +
                                String.format(Locale.ENGLISH, "%4f", team.knockoutStage.rounds.stream().filter(round -> round.name.equals(KnockoutStageRoundName.SemiFinals.name())).collect(Collectors.toList()).get(0).count.doubleValue() / (double) Util.N_SIMULATIONS) + Util.CSV_SEPARATOR +
                                String.format(Locale.ENGLISH, "%4f", team.knockoutStage.rounds.stream().filter(round -> round.name.equals(KnockoutStageRoundName.QuarterFinals.name())).collect(Collectors.toList()).get(0).count.doubleValue() / (double) Util.N_SIMULATIONS) + Util.CSV_SEPARATOR +
                                String.format(Locale.ENGLISH, "%4f", team.knockoutStage.rounds.stream().filter(round -> round.name.equals(KnockoutStageRoundName.RoundOf16.name())).collect(Collectors.toList()).get(0).count.doubleValue() / (double) Util.N_SIMULATIONS) + Util.CSV_SEPARATOR +
                                String.format(Locale.ENGLISH, "%4f", team.ability) + "\n";
                try {
                    bw.write(line);
                } catch (IOException e) {
                    log.error("could not write line {}", line);
                    e.printStackTrace();
                }
            });
        } catch (final Exception e) {
            log.error(e.getMessage());
        }
    }
}
