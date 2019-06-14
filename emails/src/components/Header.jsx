import React from 'react'
import {
  MjmlAttributes,
  MjmlBody,
  MjmlButton,
  MjmlDivider,
  MjmlFont,
  MjmlHead,
  MjmlText
} from 'mjml-react'

export default () =>
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
