
<details><summary>Learning Objectives</summary>
<br>

After completing this module, associates should be able to:

- Define Story Pointing
- Explain how the Fibonacci Sequence is used to calculate story points.
</details>
<details><summary>Description</summary>

### Agile Stories: A Refresher

Before we talk about story points in Agile, let’s remind ourselves what stories are in the Agile context.

A story is a simple, general explanation of a specific software feature. Stories are created from the customer or end-user’s perspective and show how the particular software feature will benefit the customer.

Here’s an example of a story: “I manage a small accounting team at work, and I need a way for us to easily share spreadsheets in real-time while being able to communicate with each other simultaneously.”



### What are Story Points?

Story points (SP) are a type of comparative measurement unit. They are used by agile teams to provide estimates of the total amount of work required to completely implement items from the product backlog, or user stories. As a result, estimating story points actually entails estimating work and giving each backlog item a point value.

Put another way, it’s a numeric value that helps the development team understand the effort required to complete a story. The team assigns story points based on the work's complexity, amount, and uncertainty. 

Story points in Agile are abstract measurements that developers use instead of hours. Points are relative values, so a story with a value of four is twice as hard as a story with a value of two. The actual numbers don’t matter — you could assign values between 1,000,000 and 5,000,000 if you want. Instead, you want to give the team an idea of the story’s relative difficulty. Story points tell you how much effort a given story will take to resolve.



### Story Points vary depending on:

1. **The team and individual member**:

To the relative character of story points, there is an additional dimension: the effectiveness of the team responsible for estimations. One person differs from another based on a variety of criteria, including skill level, experience, and familiarity with specific duties. Accordingly, a backlog item may be worth five points for one side and only three for another. The same holds true for individual team members; junior and senior will likewise view the "size" of the effort required for a particular activity in various ways.

2. **Another Value**:

Because a story point may only exist in relation to another value, it is a relative unit. For example, if a task requires two narrative points to finish, it represents twice as much work as a task that just requires one story point. Comparably, a task estimated at one story point will require one-third the effort compared to a task assessed at three story points. To express the effort, you might utilize Fibonacci sequence values rather than linear 1 and 2. It is up to you and your group which option you choose. Rather than the exact numbers you allocate to Agile story points, what counts most is the ratio between the story points and the relative values.

### What is a Burndown Chart?

An illustration of a team's progress through a customer's user stories in project management is provided by a burndown chart. Using an agile sprint or iteration, this tool records a feature's description from the viewpoint of the end user and compares the total effort to the amount of work.

In order to depict the past and future of the project, the amount of work still to be done is displayed on a vertical axis, and the amount of time since the project's start is displayed horizontally. As it is updated frequently for accuracy, the burndown chart is visible to all members of the agile project management team.

### Types of Burndown Charts:

There are two burndown chart variants:

1. **A Sprint Burndown**: A sprint burndown shows the amount of work left in the iteration.
2. **A Product Burndown**: A product burndown shows the amount of work left on the project plan.



### What is Fibonacci Agile Estimation?

Agile estimation refers to a way of quantifying the effort needed to complete a development task. Many agile teams use story points as the unit to score their tasks. The higher the number of points, the more effort the team believes the task will take.

The Fibonacci sequence is one popular scoring scale for estimating agile story points. In this sequence, each number is the sum of the previous two in the series. The Fibonacci sequence goes as follows: 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89… and so on.

Fibonacci agile estimation refers to using this sequence as the scoring scale when estimating the effort of agile development tasks.



### Why Use the Fibonacci Sequence for Agile Estimation?

Agile consultant Mike Cohn uses a helpful metaphor to explain why the Fibonacci sequence works well for estimating story points.

In his article on Fibonacci agile estimation, Cohn asks us to imagine holding a one-kilogram weight (2.2 pounds) in one hand and a two-kilogram weight (4.4 pounds) in the other. Without looking, could we determine which hand had a more substantial weight? Yes, easily. One is twice as heavy as the other.

But if those two weights were 20kg and 21kg, Cohn explains, we’d have more difficulty knowing which was heavier. In both scenarios, the difference in weight is one kilogram. But as we get into 20kg territory (45 pounds), the difference in the weights will need to be greater so that our brains can perceive it.

This is why Cohn recommends using the Fibonacci sequence for estimating agile story points. The numbers your team can choose from takes larger jumps as the sequence progresses, but they grow at a consistent rate — each number representing about a 60% jump. Cohn points out that even as the numbers get huge, our brains can still perceive the difference if the next number is 60% greater than the previous one.



### How Does Fibonacci Agile Estimation Work in Practice?

Imagine your team wanted to estimate the effort needed to build a new widget in your app. Everyone agreed that this task would rate a high level of difficulty and take a long time to complete it.

But now imagine your team used a linear, even-number scoring scale for story point estimation: 2, 4, 6, 8, 10… up to 50. Even if everyone agreed this new widget would be on the high end of the point scale, could you all agree whether to assign it 42 points? How about 46? Or 48?

As the numbers get higher on this scoring scale, you will find it more difficult to determine the right number because there are too many options, and the numbers at the high end aren’t distinct enough from each other.

Moreover, remember that the goal with these story points is only to estimate the level of effort. There is no reason to try to zero in on the perfect story-point score. These numbers are just a guide to help your team gauge how much time a task will take and how many resources you will need to devote to it. This is why no viable agile estimation scale uses decimals.

If your team was using the Fibonacci sequence to estimate the effort to develop this new widget, you would have only a few numbers to choose from at the top end of the scale: 34, 55, or 89. (This is where your Fibonacci agile scale would stop.)

If you do the math, you’ll see Cohn is correct that each of these numbers jumps about 60% above the previous one in the sequence. And as you can see, it would be much easier to reach a consensus on whether your widget represented a 34-point task, or 55 points, or 89.
</details>
<details><summary>Real World Application</summary>

### Why Should Teams Use Story Points in Agile?

Story points in Agile benefit development teams and product owners alike.

The effort is the result of several things coming together, and your team needs to take those into account when estimating story points. They consist of:

1. **Amount of work**: Since more work requires more effort, more work equates to more story points.
2. **Work's complexity**: An effort's level of difficulty can have a big impact.
3. **Uncertainties and risks**: Your team may need to make further efforts to handle potential risks, such as unclear requirements, legacy code lacking automated tests, and external stakeholders.

Stories points are also relative, so over time, repetition may have a role in determining how much weight you give certain actions. As a result, the "size" of the finished project will also depend on how experienced your team is with handling equivalent or related jobs.

The benefits to development teams can also vary:  


Benefits for development teams:

- The team gets a better grasp of what’s required of them, making it easier to develop a sound implementation strategy.

- The team won’t over plan, so they have a better chance of finishing an increment.

- The team knows how much to plan in a sprint, thereby letting them work at a sustainable pace.

- The team can create a reasonable estimate without having to commit to a specific timeframe.



Benefits for product owners:

- They can better assess an item’s return on investment (ROI) or the value for the money.

- Owners can better understand the technical risk associated with their more oversized items.

- They can better forecast the product’s longer-term delivery.


However, team members should be careful to avoid some common pitfalls:

- Don’t give story points to small tasks that the team can easily estimate timewise.

- Story point creation is a team effort, not a one-person show. Make sure the team stays engaged, and everyone contributes.

- Don’t average story points.

- Don’t let one variable influence the entire process — story points measure the whole picture (e.g., risk, complexity, uncertainty).



### Who Uses a Burndown Chart?

Team managers use burndown charts as a way to see the overall progress of the project and the work remaining. Developers may also use burndown charts to measure progress or to show the team what’s left to do in an Agile sprint.

Managers tend to track high-level requirements, while developers tend to track specific tasks. This is because managers will need a higher-level summary but the developers will want the specific tickets or tasks that are associated with satisfying those requirements.

### Components of a Burndown Chart:

Although the specifics can vary, it’s common to see the below sections of a burndown chart.


**Axes**:

* **The horizontal axis (X-axis)**: The X-axis typically tracks the time remaining until the project’s deadline.
* **The vertical axis (Y-axis)**: The Y-axis will track the amount of work remaining in the project. This will use different measurements depending on the project and what is being tracked. Some examples of measurement of progress are the number of Jira tickets there are left to complete, how many tasks need to be finished and how many requirements are left to satisfy.



### Ideal Work Remaining Line:

The ideal work remaining line, as its name implies, shows the amount of work that a team has left to complete under ideal circumstances at a given point in the project or sprint. Project managers estimate this baseline and draw a straight line across the burndown chart using historical data. There should always be a negative slope on the optimal work remaining line.



### Actual Work Remaining Line:

The real work remaining line shows the amount of work that a team has left to do at any given stage of the project. This is a realistic representation of the team's performance as opposed to an estimate, like the ideal work remaining line. As the team moves forward and finishes user stories, a boundary is established. Since teams work at varying speeds to finish projects, actual work remaining lines are typically not straight.


</details>
<details><summary>Implementation</summary> 

## Estimating with Story Points:

### How are story points calculated? 

Story points are essentially determined by comparing a project's features to those of a prior, comparable project. With this kind of approach, the team can comprehend the complexity of a given feature. They can also use it to assign a number that represents a specific feature effort.

**What happens if this is the team's first time estimating effort?**

A team must choose a baseline tale in such a situation. It doesn't have to be the tiniest one; just something that all the team members can relate to. After establishing the baseline tale, the group can start estimating story points by contrasting them with the baseline.

Let’s illustrate this approach using Circles as a simple example.

![Story Point Illistration](images/SP_and_BDC.png)

Circle A is clearly the smallest one. B is about double the size of A. Whereas C is roughly four, maybe five times bigger than A, and two times bigger than B. Here, we do not have precise absolute values to assign to each circle. Please note that exact absolute values were not provided, but the sizes were determined relative to Circle A as the baseline.

The following video can provide you with additional insight into agile story points:

[Agile Story Points](https://www.youtube.com/watch?v=5wc8fQn1Y_g)

### Story Points Sizing:

With an agile methodology, teams work with user stories rather than circles. Teams are able to assess the relative size of each user narrative and give them story points by comparing them with one another. Naturally, this approach works with requirements expressed in any other format as well as with user stories.

You may now be asking how the relativity ratio between individual user tales should be scaled. Naturally, there must be a greater disparity between them the larger the user story. If not, it would be challenging to distinguish between objects and estimate how much larger they actually are. Let's use circles once more to demonstrate this idea.  

![Story Point Illistration 2](images/SP_and_BDC2.png)

A and B, as well as B and D, differ significantly from one another. However, if you compare D and E, the differences between them are so slight that it is impossible to determine how much they differ. This is the reason why several Agile relative estimating methods represent narrative point values using various scales, or sizes:


* **Fibonacci sequence**: (0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89,...), where each number equals the sum of the two numbers before it.
    * **Benefits**:
        * **Non-Linear Scale**: The gaps between consecutive numbers in the Fibonacci sequence increase as the numbers grow. This non-linear scale reflects the uncertainty and variability inherent in estimating the effort or complexity of tasks.
        * **Reflects Relative Sizing**: It emphasizes the relative sizing of tasks rather than an absolute scale, making it suitable for expressing the inherent uncertainty in estimating work.



* **Linear sequence** in which the same amount is added or subtracted to go from one term to the next (e.g., 1, 2, 3, 4, 5; or 3, 6, 9, 12, 15, 18,...
    * **Benefits**:
        * **Simplicity**: Linear sequences provide a straightforward and simple scale. The effort or complexity increases uniformly with each increment.
        * **Ease of Understanding**: It can be easier for some teams, especially those new to agile practices, to work with a linear scale because of its simplicity.

## Considerations for Choosing Between Fibonacci Sequence vs Linear Sequence:

1. **Uncertainty and Non-Linearity**:
    * **Fibonacci**: Recognizes that as tasks become larger, there's often an increasing level of uncertainty and non-linearity in effort.
    * **Linear**: Assumes a more straightforward and linear relationship between the size of a task and the effort required.
2. **Ease of Use**:
    * **Fibonacci**: Some teams find the Fibonacci sequence more intuitive for capturing the relative complexity of tasks, especially when the differences between task sizes are not consistent.
    * **Linear**: Offers simplicity and ease of use, particularly when team members prefer a straightforward progression.
3. **Psychological Factors**:
    * **Fibonacci**: Can sometimes encourage teams to think in terms of general categories rather than precise values, fostering a more flexible mindset.
    * **Linear**: May make it easier for teams to make precise estimates, but could potentially lead to overemphasis on exact values.

Ultimately, the choice between the Fibonacci sequence and a linear sequence depends on the team's preferences, the nature of their work, and their comfort with the chosen scale. Many teams find success with either approach, and some even use hybrid scales that combine elements of both sequences. The key is to establish a scale that works well for the team and facilitates effective estimation and planning.


### How to Use a Burndown Chart in Agile & Scrum:

To plan and carry out projects, agile project management uses agile sprints. These are brief work periods known as sprints, during which a team works to achieve predetermined objectives that were first discussed and decided upon at the sprint planning meeting. Agile project managers find that burndown charts are quite useful since they help them stay on top of remaining work, assess performance against predetermined standards, and rapidly identify any delays. The following are instances of how an agile or scrum project can be managed with the aid of a burndown chart.

* Create a work management baseline to compare planned vs. actual work
* Complete a gap analysis based on discrepancies
* Get information for future sprint planning meetings
* Reallocate resources and manage tasks to complete sprints on time

### How to Create a Burndown Chart?

Summing the Sprint Backlog estimations for each day of the Sprint will allow you to see how much work is left, as seen in this graph. The total quantity of work left in the Sprint Backlog is the amount of work that is still unfinished for each Sprint. Using the daily totals you keep track of, make a graph that illustrates the amount of work left over over time.

### Burndown Chart Example:

* Duration: 5 days
* Sprint Backlog: 8 tasks
* Velocity: 80 available hours



**Step 1** – Create Estimate Effort

Suppose your ideal baseline for using the available hours over the sprint.  So in the simplest for this is the available hours divided by number of days.  In this example, 80 hours over 5 days equating to 16 hours a day. In order to create the project burn-down chart, the data needs to be captured as a daily running total starting with 80 hours than 64 hours left 1 (80 – 16) at end of day, 48 hours left at end of day 2, etc.

### Burndown- Estimate effort:  

| Day	| Effort Remaining|
|-------|-----------------|
|  0	|  80             |
|  1	|  64             |
|  2	|  48             |
|  3	|  32             |
|  4	|  16             |
|  5	|  0              |

**Step 2** – Track Daily Process

The daily progress is then captured in the table against each task.  It is important to remember that the value captured for each day is the estimated effort to complete the task, not the actual effort.  

|Task	  | Hours |Day - 1|Day - 2 |Day - 3	|Day - 4|Day - 5|Total|
|---------|-------|-------|--------|--------|-------|-------|-----|
|Task - 1 |	10	  |  3	  |  2	   | 0	    | 1	    | 4	    | 10  |
|Task - 2 |	10	  |  3	  |  2	   | 0	    | 1	    | 4	    | 10  |
|Task - 3 |	10	  |  3	  |  2	   | 0	    | 1	    | 4	    | 10  |
|Task - 4 |	10	  |  3	  |  2	   | 0	    | 1	    | 4	    | 10  |
|Task - 5 |	10	  |  3	  |  2	   | 0	    | 1	    | 4	    | 10  |
|Task - 6 |	10	  |  3	  |  2	   | 0	    | 1	    | 4	    | 10  |
|Task - 7 |	10	  |  3	  |  2	   | 0	    | 1	    | 4	    | 10  |
|Task - 8 |	10	  |  3	  |  2	   | 0	    | 1	    | 4	    | 10  |

**Step 3** – Compute the Actual Effort

The total remaining effort needs to be captured at the end of each day.  This is the total (sum) of all of the estimated time remaining at the end of each day.  

|Days|	Actual Effort |	Effort Remaining|
|----|----------------|-----------------|
|0	 |80	          |80               |
|1	 |56	          |64               |
|2	 |40	          |48               |
|3 	 |40	          |32               |
|4	 |32	          |16               |
|5	 |0	              |0                |

**Step 4** – Obtain the Final Dataset

When the data is available, the project burn-down chart can be created.  This is relatively simple using the line chart option available within Excel.  

|Days|	Actual Effort |	Effort Remaining|
|----|----------------|-----------------|
|0	 |80	          |80               |
|1	 |56	          |64               |
|2	 |40	          |48               |
|3 	 |40	          |32               |
|4	 |32	          |16               |
|5	 |0	              |0                |

Highlight the summary table that contains the daily total for baseline effort and estimated effort.  You should also capture the heading of time period (Day 0, Day 1, etc).

**Step 5** – Plot the Burndown using the Dataset

It is very simple to create a project burn-down chart as following, as long as you know what data you are tracking.

### Burndown chart example:
![Burndown Chart Example](images/SP_and_BDC3.png)
</details>
<details><summary>Summary</summary> 
<br>

- Story points are units of measurement used to determine how much effort is required to complete a product backlog item or any other piece of work. 

  - The team assigns story points based on the work's complexity, amount, and uncertainty.

- Story Points are a numeric value that helps the development team understand how challenging the story is.

- The Fibonacci sequence is one popular scoring scale for estimating agile story points. 

  - In this sequence, each number is the sum of the previous two in the series. 

  - The Fibonacci sequence goes as follows: 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89… and so on.

  - Each of these numbers jumps about 60% above the previous one in the sequence after the first few terms which makes the sequence ideal for assigning story points since numbers on the high end are more distinct.
</details>

