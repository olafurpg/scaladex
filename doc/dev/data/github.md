# Github API

## Authentication
create token here: https://github.com/settings/tokens/new

Example:

curl \
  -H "Authorization: token xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" \
  https://api.github.com/repos/MasseGuillaume/ScalaKata2/readme


## Repo

### Info

[doc](https://developer.github.com/v3/repos/#get)

`curl https://api.github.com/repos/typelevel/cats`

### Contributors

[doc](https://developer.github.com/v3/repos/contributors/)

```
curl \
  -H "Authorization: token xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" \
  https://api.github.com//repos/typelevel/cats/contributors
```

### README API

[doc](https://developer.github.com/v3/repos/contents/#get-the-readme)

Example:

`curl -H "Accept: application/vnd.github.VERSION.html" https://api.github.com/repos/MasseGuillaume/ScalaKata2/readme`


