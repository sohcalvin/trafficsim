/* global module */

module.exports = function(grunt) {
	"use strict";
	grunt.initConfig({
		// starts a server under localhost:8081
		connect: {
			dev: {
				options: {
					base: ".",
					keepalive: "true",
					hostname: "localhost",
					port: 8081
				}
			}
		},


	});

	// Load npm packages

	grunt.loadNpmTasks("grunt-contrib-connect");
		
	/* run `grunt serve`
	 * Starts local web server at localhost:8081
	 */
	grunt.registerTask("serve", [
		"connect:dev"
	]);


};
