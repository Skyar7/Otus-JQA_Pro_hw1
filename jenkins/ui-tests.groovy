timeout(60) {
    node("maven") {
        wrap([$class: 'BuildUser']) {
            currentBuild.description = """
build user: ${BUILD_USER}
branch: ${REFSPEC}
"""

            config = readYaml text: env.YAML_CONFIG ?: null;

            if (config != null) {
                for (param in config.entrySet()) {
                    env."${param.getKey()}" = param.getValue()
                }
            }
        }

        stage("Checkout") {
            checkout scm;
        }
        stage("Create configuration") {
            sh "echo BROWSER_NAME=${env.getProperty('BROWSER_NAME')} > ./.env"
            sh "echo BROWSER_VERSION=${env.getProperty('BROWSER_VERSION')} >> ./.env"
            sh "echo BASE_URL=${env.getProperty('BASE_URL')} >> ./.env"
            sh "echo REMOTE_URL=${env.getProperty('REMOTE_URL')} >> ./.env"
            sh "echo DRIVER_TYPE=${env.getProperty('DRIVER_TYPE')} >> ./.env"
        }
        stage("Run UI tests") {
            sh "mkdir ./reports"
            sh "docker run --rm --env-file -v ./reports:root/ui_tests_allure-results ./.env -t ui_tests:${env.getProperty('TEST_VERSION')}"
        }
        stage("Publish allure results") {
            REPORT_DISABLE = Boolean.parseBoolean(env.getProperty('REPORT_DISABLE')) ?: false
            allure([
                    reportBuildPolicy: 'ALWAYS',
                    results: ["./reports", "./allure-results"],
                    disabled: REPORT_DISABLE
            ])
        }
    }
}