The following defines a structured approach to defining specific customer scenarios and their corresponding solutions.

The Arc Framework combines this use case format with simple prompting and static code to create a more reliable and predictable Agent/LLM behavior.

Breakdown of the Format

Each use case typically consists of the following components:

UseCase Name: A concise, descriptive id that uniquely identifies the use case. Should be in lowercase and use underscores to separate words.

Description: A detailed explanation of the customer's situation or query.

Steps: A sequence of steps for the Agent to perform before providing the final solution. This section is optional and can be used to provide additional context or guidance.

Solution: The recommended solution to resolve the issue or fulfill the customer's request.

Alternative Solution: An alternative solution that the Agent should try if the primary solution is not effective.

Fallback Solution: The fallback solution that the Agent should use if the primary and alternative solutions fail. The fallback solution is to prevent the Agent from getting stuck in a loop where it provides the same solution over and over again. It is triggered after X number of failed attempts, X being a configurable parameter.

Examples: A list of example queries or statements that should trigger this use case. This section is optional and can be used to provide additional context or guidance.

Example:

### UseCase: password_reset
#### Description
Customer has forgotten their password and needs to reset it.

#### Steps
- Ask the customer for their registered email address.
- Send a password reset link to the provided email address.

#### Solution
Guide the customer through the password reset process defined on the webpage https://www.example.com/reset-password.

#### Fallback Solution
If the customer cannot access their email, escalate the issue to a higher tier of support.

#### Examples
 - I forgot my password.


If the Agent has access to tools, such as send_password_reset_link, these would be called as part of this use case.

Guidelines

Use consistent terms and language throughout the use case to ensure clarity and avoid confusion. For example, if you refer to the user as "customer" in one section, use "customer" throughout the document. (Prefer "customer" over "user" as it is more specific and helps maintain a customer-centric focus.)

Providing examples when necessary. Examples are a very powerful construct and should only be used if the Agent struggles to understand the use case.

Conditionals

Conditionals is a feature that enables us to omit lines from the use cases based on certain conditions. Conditionals are defined in brackets, for example, <condition1, condition2>. Each line containing such a conditional is filtered out before being provided to the Agent unless all conditions are met. Conditionals can be placed anywhere within the line.

Example:

### UseCase: password_reset
#### Description
Customer has forgotten their password and needs to reset it.

#### Solution
<isBusinessCustomer>Provide the webpage https://www.example.com/business/reset-password.
Provide the webpage https://www.example.com/reset-password.<isPrivateCustomer>

The conditions are passed to the useCases function as a set of strings.

 useCases("use_cases.md", conditions = setOf("isBusinessCustomer"))

This would produce the following output that would be feed to the Agent:

### UseCase: password_reset
#### Description
Customer has forgotten their password and needs to reset it.

#### Solution
Provide the webpage https://www.example.com/business/reset-password.

Important Conditionals are only supported in the body of Steps, Solution, Alternative Solution and Fallback Solution.

