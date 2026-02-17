def call(Map args = [:]) {
  boolean forceAbort = (args.get('abortPipeline', false) as boolean)

  String branch = args.get('branchName', null)
  if (!branch) { branch = env.BRANCH_NAME ?: env.GIT_BRANCH ?: 'unknown' }

  boolean shouldAbort
  if (forceAbort) {
    shouldAbort = true
  } else if (branch == 'master') {
    shouldAbort = true
  } else if (branch.startsWith('hotfix')) {
    shouldAbort = true
  } else {
    shouldAbort = false
  }

  echo "Branch detected: ${branch} | abortOnQualityGateFailure=${shouldAbort}"

  withSonarQubeEnv('SonarQube') {
    if (isUnix()) {
      sh 'echo "Ejecucion de las pruebas de calidad de codigo"'
    } else {
      bat 'echo Ejecucion de las pruebas de calidad de codigo'
    }

    // Mantener timeout como pide el enunciado (aunque sea mock)
    timeout(time: 5, unit: 'MINUTES') {
      echo "Mock Quality Gate: PASSED"
      if (shouldAbort) {
        error("Mock Quality Gate: FAILED (abortPipeline habilitado por rama)")
      }
    }
  }
}

