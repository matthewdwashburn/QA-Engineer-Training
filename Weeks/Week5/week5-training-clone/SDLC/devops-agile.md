# DevOps and Agile (vs Waterfall)
Agile is a mentality or philosophy utilized when approaching the creation of information systems, and is a flexible approach of addressing the steps of the Software Development Life Cycle.

### References
References are resources (either external or internal) that trainers and associates can use to lookup information about the technology - typically documentation, notes, videos, or tutorials
* [Agile and DevOps - Atlassian](https://www.atlassian.com/agile/devops)
* [How to Combine DevOps and Agile](https://devops.com/how-to-combine-devops-and-agile/)
* [DevOps Culture - Martin Fowler](https://martinfowler.com/bliki/DevOpsCulture.html)
* [Waterfall vs Agile - Atlassian](https://www.atlassian.com/agile/project-management/program)
* [Waterfall vs Agile - Guru99](https://www.guru99.com/waterfall-vs-agile.html)

## SDLC
The Software Development Life Cycle [SDLC] outlines the process to plan, create, test and deploy information systems and applications. 

SDLC follows these General Steps:
* Requirements Phase
  * The existing system (if any) is evaluated, and determinations are made to address existing flaws or systems necessary for the new/improved functionality desired.
  * Performed by: Business Analysts.
* Analysis Phase
  * The system requirements are defined for the new system. Particularly, deficienceies and proposals to improve the system are  addressed.
  * Performed by: Business Analysts with Collaboration from Senior Members of staff.
* Design Phase
  * The proposed system is designed and product features are mapped. Does not involve the production of actual code.
  * Performed by: System Architects and Senior Developement staff.
* Development Phase
  * Production of actual code to build systems.
  * Performed by: Development team
* Testing Phase
  * Software is tested against system requirements to ensure intended functionality.
  * Performed by: Development team & Testing team
* Deployment and Maintenance Phase
  * Product is deployed to customer or end users. System is maintained if/when issues arise.
  * Performed by: Operations team with possible input from Development team as needed

Practicing SDLC refers to the methodology utilized when performing these outlined steps.

## Waterfall
A linear approach to following the SDLC. Traditionally, a waterfall method only allows for forward movement through each phase of the SDLC. A waterfall process sees a single planning stage at the start of development, followed by the delivery and review of each SDLC phase in sequential order. Once a phase is considered completed (and approved after a review), development moves to the next stage. If requirements change or issues arise, then the progress must be halted and the product must be re-evaluated at the Requirements level.

### Benefits to Waterfall
* Incredibly easy to manage the workflow because there are specific deliverables and review process for each phase.
* Well suited for small teams or short-term projects that will not require any changes to the original specifications.
* Generally results in a faster delivery of product.
* Process and results can be easily documented.
* Easy to adapt to shifting teams since the steps of each phase is clearly outlined.

### Risks/Considerations
* Method can be quite inflexible and inefficient
* Not ideal for large teams or large projects
* Testing does not begin until after development has completed, meaning that it is more prone to bugs.

![Waterfall Methodology - Visualized](./waterfall.png)

## Agile
An approach to SDLC that is based on iterative development; wherein, requirements, solutions and systems evolve throughout the production of software, and collaboration between cross-functional teams. Due to its flexibility Agile is considered the standard for development.
* More widely accepted and utilized method of following the SDLC.
* Follows Four Core Values from the [Agile Manifesto](https://agilemanifesto.org/iso/en/manifesto.html):
  * **Individuals and Interactions** over processes and tools
  * **Working Software** over comprehensive documentation
  * **Customer Collaboration** over contract negotiation
  * **Responding to change** over following a plan

Agile methods (often referred to as frameworks) are comprehensive approaches to the phases of the SDLC.
* "Scrum" is the most common Agile framework
  * Fun Fact: 'Scrum' gets its name from rugby. Short for scrummage, which is a method to restart play during a match of rugby football, involving players to pack closely together in an attempt to gain possession of the ball. Scrums require teamwork and cooperation to find the greatest success.
*Agile Scrum Methodology
  * In a "Scrum" the entire project is divided into "sprints".
  * A sprint is a smaller, isolated task or system, completed within a specified timeframe, which is required for a software development product.

### Benefits of Agile
* Client Collaboration is generally regarded positively
* Agile team cultures tend to stay more self-organized and motivated
* Overall quality of product is usually higher due to iterative nature
* Less risk in development process due to incremental nature of development

### Risks/Considerations
* Not as useful for smaller development projects
* Generally there are higher costs associated with an Agile workflow
* Development time can bloat if managed improperly or requirements are not clear during each step of the development
* Requires more experienced members during the planning and management of projects

## DevOps with Agile
As the development and deployment of applications became increasingly important, DevOps and Agile have increased in popularity as methods to optimize the process of software production. DevOps originally sought to reduce the number of manual steps necessary to produce software, resulting in more streamlined and faster releases. Agile practices encourage collaboration and re-evaluation of systems in favor of improvements. The separation of traditional DevOps and Agile approaches can lead to mismanagement of infrastructure as it can lead to a mentality of "it's someone else's problem".

Many tasks in operations can be planned for, including system upgrades, moving to new datacenters or releasing large system changes. However, many unplanned changes can arise in operations, such as system outages, performance spikes, or compromised security. Since these problems require immediate responses, and as such Agile is a useful approach to solving these operational problems, due to their unpredictable nature. DevOps seeks to eliminate this unpredictability by having a set of guidelines and a systematic approach to the integration, development, and eventual deployment of code. DevOps also seeks to eliminate the barrier between Development and Operations by integrating development systems with testing and deployment.

Though DevOps (automating structured tasks to accomplish specific goals and produce completed products), and Agile (adapting to changing workflow or new requirements rather than following formulaic plans) seem to be contrary, both methodologies uderlyingly serve the same purpose: To create working and valuable code as quickly and efficiently as possible. DevOps actually inherently adopts many Agile methodologies, as it encourages the collaboration of development and operations when creating software products.
* Agile Practices with DevOps:
  * Continuous Integration
  * Continuous Delivery
  * Continuous Deployment

Generally, there are three ways of approaching DevOps culture.
1. System Thinking: Placing emphasis on the performance of an entire system, as opposed to a specific silo of work or department.
1. Amplify Feedback Loops: Through the use of automation, the speed and efficiency of feedback loops can drastically improve.
1. Culture of Continual Experimentation and Learning: Fostering a work culture which values taking risks, learning from failure, and understanding that practice and repetition are the prerequisites to mastery, leads to increased collaboration and self-reflection. This promotes further improvement for an entire workplace community, and is even reflected through Agile retrospective meetings.

These ways are not necessarily mutually exclusive, and the integration of these approaches, with Agile philosophies can lead to:
* More streamlined processes
* Better collaboration
* Fewer bugs and faster fixes
* Higher overall quality of product
