def call(Map args = [:]) {
  boolean abortPipeline = (args.get('abortPipeline', false) as boolean)

  // Si es multibranch, env.BRANCH_NAME suele existir
  String branch = args.get('branchName', null)
  if (!branch) { branch = env.BRANCH_NAME ?: env.GIT_BRANCH ?: 'unknown' }

  // Ejercicio 4: lógica de aborto por rama
  boolean shouldAbort
  if (abortPipeline) {
    shouldAbort = true
  } else if (branch == 'master') {
    shouldAbort = true
  } else if (branch.startsWith('hotfix')) {
    shouldAbort = true
  } else {
    shouldAbort = false
  }

  echo "Branch detected: ${branch} | abortOnQualityGateFailure=${shouldAbort}"

  // Mantener sonarenv + timeout como pide la práctica
  withSonarQubeEnv('SonarQube') {
    if (isUnix()) {
      sh 'echo "Ejecucion de las pruebas de calidad de codigo"'
    } else {
      bat 'echo Ejecucion de las pruebas de calidad de codigo'
    }

    timeout(time: 5, unit: 'MINUTES') {
      // OJO: waitForQualityGate requiere el plugin de SonarQube para Jenkins
      def qg = waitForQualityGate(abortPipeline: shouldAbort)
      echo "QualityGate status: ${qg.status}"
    }
  }
}
