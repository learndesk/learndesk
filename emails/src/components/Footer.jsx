import React from 'react'
import {
  Mjml,
  MjmlAttributes,
  MjmlBody,
  MjmlButton,
  MjmlColumn,
  MjmlDivider,
  MjmlFont,
  MjmlHead, MjmlImage,
  MjmlSection,
  MjmlText
} from 'mjml-react'
import String from '../i18n'

/*
      <mj-button background-color="#f49898" font-family="Questrial, sans-serif" font-size="18px"/>
      <mj-divider border-color="#cbc7c4" border-width="1px" padding-top="5px" padding-bottom="5px" padding-left="0" padding-right="0" />

 */

export default ({ locale, solicited, children }) =>
  <Mjml>
    <MjmlHead>
      <MjmlFont href='https://fonts.googleapis.com/css?family=Questrial' name='Questrial'/>
      <MjmlAttributes>
        {/* eslint-disable react/no-children-prop */}
        <MjmlBody
          backgroundColor='#fff'
          children='a'
        />
        <MjmlText
          fontSize={22}
          lineHeight={28}
          color="#343434"
          fontFamily="Questrial, sans-serif"
          children='a'
        />
        <MjmlButton
          fontSize={22}
          backgroundColor="#f49898"
          fontFamily="Questrial, sans-serif"
          children='a'
        />
        <MjmlDivider
          borderColor="#cbc7c4"
          borderWidth={1}
          paddingTop={5}
          paddingBottom={5}
          paddingLeft={0}
          paddingRight={0}
          children='a'
        />
        {/* eslint-enable react/no-children-prop */}
      </MjmlAttributes>
    </MjmlHead>
    <MjmlBody>
      <MjmlSection>
        <MjmlColumn>
          <MjmlImage
            src="https://cdn.bowser65.xyz/learndesk/logo.png"
            href="https://learndesk.app"
            align="center"
            padding={0}
            width={400}
          />
          <MjmlDivider borderColor="#f49898" borderWidth={4} paddingTop={0}/>
        </MjmlColumn>
      </MjmlSection>
      {children}
      <MjmlSection backgroundColor="#efefef" padding={0}>
        <MjmlColumn verticalAlign="middle" width="25%">
          <MjmlImage src="https://cdn.bowser65.xyz/learndesk/squirrel_b.png" padding={0}/>
        </MjmlColumn>
        <MjmlColumn verticalAlign="top" width="75%">
          <String
            locale={locale}
            string={solicited ? 'footer.solicited' : 'footer.registered'}
            paddingBottom={5}
            lineHeight={16}
            fontSize={14}
          />
          <String
            locale={locale}
            string='footer.question'
            paddingBottom={5}
            lineHeight={16}
            fontSize={14}
          />
          <String
            locale={locale}
            string='footer.noreply'
            paddingBottom={5}
            lineHeight={16}
            fontSize={14}
          />
        </MjmlColumn>
        <MjmlColumn verticalAlign="middle" width="100%">
          <MjmlDivider paddingBottom={0}/>
          <MjmlText align="center" fontSize={14}>
            <a style={{ fontSize: 14, color: '#34383b' }} href="https://learndesk.app">Learndesk</a>
            &nbsp;•&nbsp;
            <a style={{ fontSize: 14, color: '#34383b' }} href="https://learndesk.app/legal/terms">Terms of Service</a>
            &nbsp;•&nbsp;
            <a style={{ fontSize: 14, color: '#34383b' }} href="https://learndesk.app/legal/privacy">Privacy Policy</a>
          </MjmlText>
        </MjmlColumn>
      </MjmlSection>
    </MjmlBody>
  </Mjml>
