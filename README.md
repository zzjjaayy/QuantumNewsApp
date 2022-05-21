# QuantumNewApp

Take home assignment from Quantum Innovations

## Major Libraries used →

- Firebase →
    - This one is pretty obvious given the authentication needs of the app, The email, google and facebook authentication is being handled through firebase auth.
- Retrofit w/ Gson →
    - This one was used to make API calls and parse the json to data classes automatically
- Glide →
    - A very image loading library for news images
- Country code picker →
    - This was used to remove the tedious jo of getting country codes and validating → [https://github.com/hbb20/CountryCodePickerProject](https://github.com/hbb20/CountryCodePickerProject)
- Times formatter →
    - Formatting the published time to time difference {pretty niche use case but made life much easier}
- Android custom tabs →
    - Loading the news url in web views {wasn’t part of the requirements but felt incomplete without it}
- Splash compat library →
    - Recently shifted to release candidate, this library makes the sweet sweet Android 12 splash screens available for older devices.
- Other jetpack libraries like Lifecycle component or kotlin libraries like coroutines.
