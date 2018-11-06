# Boilerplate App for Angular 5 Apps

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 1.4.5.

## Development server

Run `npm start` for a dev server. The app will automatically reload if you change any of the source files.

You should update your MAMP .htaccess file as usual with a new mapping to this app, so you can remove
the port number i.e.
`# Store Account.
RewriteRule ^static/?(.*)$ http://localhost:4200/$1 [P,L,QSA]`

Navigate to `http://localhost/static/`

## Build

Run `npm run build` to build the project. The build artifacts will be stored in the `dist/` directory.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).
