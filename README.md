# Vivira task

This is the repository for the vivira coding challenge, which takes a text input to search for GitHub repositories' names, and displays the results in a list.

## Dependencies
Hilt for dependency injection.
Retrofit/okkhttp for network calls.
Glide for remote image loading.

## Development Notes 
The api service can also potentially be included through dependency injection, to abstract the data sources further.
Pagination is simplified, not including the usual offsets, but rather the page parameter already included in the GitHub response.

## Notes on source control
For simplicity of the excercise , the commits are added on the main branch. In a usual scenario, features would be added in a dedicated feature branch, and then merged into the branch via pull requests.

