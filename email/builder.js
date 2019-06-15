/*
 * Learndesk REST API
 * Copyright (C) 2019 Learndesk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
