# Changelog

## [2.4.0] - 2021-08-04
## Added
- Added method `startServerOnRandomPort` to start a new server on a random port
  and handle retries.

## Changed
- Method `handleRequest` of `HttpServlet` is no longer `final` and can be overridden.

## Fixed
- Ignoring headers with `null` values.

## [2.3.0] - 2020-09-28
### Fixed
- Charset is not added to the `Content-Type` header anymore when the mime type is set but not the charset.

## [2.2.0] - 2020-09-04
### Added
- It is now possible to select a server type between LocalServer's own
  implementation or Sun's implementation.

## [2.1.0] - 2020-09-03
### Added
- Added an adapter to use instances of `com.github.raphcal.localserver.HttpServlet`
  with `com.sun.net.httpserver.HttpServer`.

## [2.0.1] - 2020-04-23
### Added
- Added a changelog.

### Fixed
- Http responses are no longer truncated.
- Fixed build on JDK >= 11.

## [2.0.0] - 2019-11-15
### Breaking change
- Using Java 8 and try with resources.

### Added
- Trying to bind to port + 1 if the given port is already bound.

### Changed
- Started the translation of comments in English.

## [1.0.1] - 2019-01-24
### Fixed
- Casting ByteBuffer to Buffer to avoid errors with Java < 9.

## [1.0.0] - 2019-01-23
