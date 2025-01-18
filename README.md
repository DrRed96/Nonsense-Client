# Nonsense Client
Nonsense Client is a free and open source hack client for Minecraft 1.8.8.<br>
More information at https://bhopper.wtf/nonsense

![Click GUI](/.github/images/clickgui.png)

## Installation
- The launcher can be downloaded [here](https://bhopper.wtf/nonsense/download/Nonsense.zip).

## IntelliJ Setup
1. Download GraalVM JDK 23 (https://www.graalvm.org/downloads/).
2. Clone this repository.
3. Open the cloned repository in IntelliJ.
4. Go to `File -> Project Structure -> Project` then set the SDK to `GrallVM 23` and set the Language Level to `Java 23`).
5. Make sure you have launched Minecraft 1.8.8 at least once, run the `client -> copyAssets` gradle task.
6. To run the client, run the `Start` run configuration.

## Contributing Guidelines
- Specify the changes you have made in your pull request.
- Don't commit any IDE or system related files, these should be added to the `.gitignore`.
- Use appropriate code formatting and naming conventions, use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) as reference.

## FAQ
- **Can I use Nonsense Client's source code for my own client?**<br>
  Yes! You may use this clients source code for your own client as stated in the ["Unlicense"](./UNLICENSE).<br>
  Credit is appreciated but not required.
