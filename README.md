#HOWTO
First, install [sbt](http://www.scala-sbt.org/0.13/docs/Setup.html). Then, in the root directory, simply run `sbt run` to start the tool, and go to [localhost:8080]() to interact with it.

To create a new team, enter the team name in the topmost field, and press enter or hit the "Create Team" button. Then add individual members to each team using their respective fields. Delete members one by one by selecting them in the list and hitting the "Delete Member" button. Finally, delete a team by clicking its "Delete Team" button.

Once all the teams are set up, simply select how many weeks for which to create schedules, and click the "Make Schedules" button. You can also edit the `teams.json` file before starting the tool to edit teams faster. WARNING: all changes made to `teams.json` while the tool is running will be overwritten. On the other hand, the `teams.json` file will always be kept up to date with regards to what the tool is showing.

#Strategy
We can think of the set of employees as a graph, where employees are only connected to teammates. For every week, we keep track of which employees have already been matched, and which employees still need to be matched with whom. To maximize the number of matched employees, we first find the employee that has the fewest available options, with regards to who is still available and who they have yet to be matched with. We match the employee the teammate that has the fewest options. We keep doing this for a given week untill no more pairs can be made. By always picking the employee with the fewest options, we ensure that at least no employee is left unmatched for extended periods of time. Similarily, highly connected employee will eventually have few options remaining and will eventually get priority.

For lack of a formal proof for this algorithm, it can at least be shown that:
+ For a given team with members that do not belong to any other team, this algorithm always picks the optimal scheduling, thanks to the fact that there is no suboptimal order.
+ This algorithm always halts, because it removes a pair of nodes and an edge from the graph at each iteration, and the graph contains a finite number of nodes and edges.
+ For a fully connected graph with n nodes, computing the schedule for a given week is of the following complexity:
  + For each node, every neighbor must be checked for availability to create a new, filtered subgraph of the original employee connectivity graph. This is at worst an O(n^2) operation, though since the graph gets smaller and smaller every time we remove a pair, this is expected to be much faster.
  + Finding the next best employee in the new subgraph is O(n) and finding the next best neighbor is also O(n), therefore this is an O(n) operation.
  + Finally, at most n/2 pairs can be made, thefore the above two steps are repeated O(n/2) times, and finding the optimal schedule for a given week is therefore O(n/2 * (n + n^2)) = O(n^3)

#Testing
I first always test the immediate evident behavior. In this case, I make sure that the Scheduler class throws errors when the teams it is given cannot form schedules, or are otherwise generally invalid.

After that, I tested for the conditions explicitly set by the prompt. In this case, I test that after 100 weeks' worth of schedules, no employee is:
+ Scheduled more than once per week
+ Matched with a team mate before having been matched with every other teammate.

After that, it is harder to judge the performance of the algorithm itself, since the optimal schedule is not always immediately evident. For example, I can test that for a single team with an even number of members, everyone is scheduled every week (since in this case, that's what the optimal schedule is)

#Comments
All in all, this was a really fun challenge. The part that was particularly difficult was wrapping my head around the actual implementation of the algorith, which sounds great on paper, but is hard to implement in a way that is legible or even understandable. I find it a shame that I wasn't able to formally prove the optimality of the algorithm though. Would be nice to know that it actually does work as expected in all cases.

Otherwise, making the tool useable was tricky. It required me to play with some of Scala's web frameworks, and general HTML, which I was not too familiar with. But I'm pretty proud of the result, especially in how easy to use the interface ended up being.