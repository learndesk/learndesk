/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/ocl>.
 */

const mjml2html = require('mjml')
const rimraf = require('rimraf')
const path = require('path')
const fs = require('fs')

const htmlPath = path.resolve(__dirname, '..', 'src', 'main', 'resources', 'email', 'html')
rimraf.sync(htmlPath)
fs.mkdirSync(htmlPath)

function buildFile (file) {
  const mjml = fs.readFileSync(file, 'utf8')
  const { html, errors } = mjml2html(mjml, {
    minify: true,
    filePath: file,
    keepComments: false,
    validationLevel: 'strict'
  })

  if (errors.length > 0) {
    throw new Error(errors)
  }

  fs.writeFileSync(path.resolve(htmlPath, path.basename(file).replace('mjml', 'html')), html)
}

buildFile(path.resolve(__dirname, 'mjml', 'data_harvest.mjml'))
buildFile(path.resolve(__dirname, 'mjml', 'email_change_confirm.mjml'))
buildFile(path.resolve(__dirname, 'mjml', 'email_change_notification.mjml'))
buildFile(path.resolve(__dirname, 'mjml', 'email_confirm.mjml'))
buildFile(path.resolve(__dirname, 'mjml', 'password_reset.mjml'))
