
# [1.1.2](https://github.com/asterics/WebACS/compare/v1.1.1...v1.1.2) (2019-09-05)

* **https redirection bug fix** If the original URL had query parameters (e.g. ?autoDownloadModel=true), they were lost through redirection. (see: https://github.com/asterics/WebACS/commit/7b55b1bfb739b1b47ca901743f0e8ccd0336a7e4) 

# [1.1.1](https://github.com/asterics/WebACS/compare/v1.1.0...v1.1.1) (2019-06-28)

* **https to http redirect** if the WebACS is loaded with https:// it is automatically redirected to http:// (see https://github.com/asterics/WebACS/commit/86f885652ee32af9672f39354f80fc6bb244203b)
* update of default component collection
* increased canvas size to 4000x2000

# [1.1.0](https://github.com/asterics/WebACS/compare/v1.0.0...v1.1.0) (2019-04-24)


### Features

* **dynamic properties:** load dynamic runtime properties from the ARE during execution ([6e26a7b](https://github.com/asterics/WebACS/compare/ca03387...6e26a7b))


### Bugfixes

* **editable properties:** defer loading of models from url query ([ca03387](https://github.com/asterics/WebACS/commit/ca03387))


# [1.0.0](https://github.com/asterics/WebACS/releases/tag/v1.0.0) (2018-11-26)